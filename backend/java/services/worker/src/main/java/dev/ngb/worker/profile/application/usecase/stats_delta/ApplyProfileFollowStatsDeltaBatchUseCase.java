package dev.ngb.worker.profile.application.usecase.stats_delta;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.model.stats.ProfileStatsCountDelta;
import dev.ngb.worker.profile.application.port.ProfileStatsDeltaEventDedupePort;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.event.ProfileFollowStatsDeltaEvent;
import dev.ngb.util.batching.BatchExecutorUtils;
import dev.ngb.util.batching.core.ItemProcessor;
import dev.ngb.util.batching.core.ItemReader;
import dev.ngb.util.batching.core.ItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Applies {@link ProfileFollowStatsDeltaEvent} payloads in batch: dedupes by event uuid, aggregates per profile,
 * then issues bulk counter updates.
 */
@Slf4j
@RequiredArgsConstructor
public class ApplyProfileFollowStatsDeltaBatchUseCase implements UseCaseService {

    private static final int PIPELINE_SHARDS = 1;
    private static final int PIPELINE_BUFFERED_ITEMS = 2_000;
    private static final int PIPELINE_WRITE_BATCH_SIZE = 200;
    private static final int PIPELINE_PROCESSOR_CONCURRENCY =
            Math.max(Runtime.getRuntime().availableProcessors(), 4);
    private static final int PIPELINE_MAX_INFLIGHT = 4_000;

    private final ObjectMapper objectMapper;
    private final ProfileStatsDeltaEventDedupePort dedupeRepository;
    private final ProfileStatsRepository profileStatsRepository;

    public void execute(List<String> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return;
        }
        AtomicInteger cursor = new AtomicInteger(0);
        ItemReader<String> reader = (_, _) -> {
            int index = cursor.getAndIncrement();
            if (index >= payloads.size()) {
                return null;
            }
            return payloads.get(index);
        };
        ItemProcessor<String, List<ProfileStatsCountDelta>> processor = raw -> {
            if (raw == null) {
                return null;
            }
            ProfileFollowStatsDeltaEvent event = parse(raw);
            String uuid = event.uuid();
            if (!dedupeRepository.tryClaimEvent(uuid)) {
                return null;
            }
            List<ProfileStatsCountDelta> deltas = new ArrayList<>(2);
            if (event.followerDelta() != 0) {
                deltas.add(new ProfileStatsCountDelta(event.targetProfileId(), event.followerDelta(), 0L));
            }
            if (event.followingDelta() != 0) {
                deltas.add(new ProfileStatsCountDelta(event.followerProfileId(), 0L, event.followingDelta()));
            }
            return deltas.isEmpty() ? null : deltas;
        };
        ItemWriter<List<ProfileStatsCountDelta>> writer = this::flushAdjustments;

        try {
            BatchExecutorUtils.execute(
                    PIPELINE_SHARDS,
                    reader,
                    processor,
                    writer,
                    PIPELINE_BUFFERED_ITEMS,
                    PIPELINE_WRITE_BATCH_SIZE,
                    PIPELINE_PROCESSOR_CONCURRENCY,
                    PIPELINE_MAX_INFLIGHT
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Profile follow stats delta pipeline failed", ex);
        }
    }

    private void flushAdjustments(List<List<ProfileStatsCountDelta>> batchedDeltaLists) {
        if (batchedDeltaLists == null || batchedDeltaLists.isEmpty()) {
            return;
        }
        Map<Long, long[]> merged = new HashMap<>();
        for (List<ProfileStatsCountDelta> deltas : batchedDeltaLists) {
            if (deltas == null || deltas.isEmpty()) {
                continue;
            }
            for (ProfileStatsCountDelta d : deltas) {
                long[] acc = merged.computeIfAbsent(d.profileId(), k -> new long[2]);
                acc[0] += d.followerDelta();
                acc[1] += d.followingDelta();
            }
        }
        List<ProfileStatsCountDelta> adjustments = new ArrayList<>(merged.size());
        for (Map.Entry<Long, long[]> e : merged.entrySet()) {
            long followerDelta = e.getValue()[0];
            long followingDelta = e.getValue()[1];
            if (followerDelta != 0L || followingDelta != 0L) {
                adjustments.add(new ProfileStatsCountDelta(e.getKey(), followerDelta, followingDelta));
            }
        }
        profileStatsRepository.adjustCountsBulk(adjustments);
    }

    private ProfileFollowStatsDeltaEvent parse(String json) {
        try {
            return objectMapper.readValue(json, ProfileFollowStatsDeltaEvent.class);
        } catch (JacksonException ex) {
            throw new IllegalArgumentException("Invalid ProfileFollowStatsDeltaEvent JSON", ex);
        }
    }

}
