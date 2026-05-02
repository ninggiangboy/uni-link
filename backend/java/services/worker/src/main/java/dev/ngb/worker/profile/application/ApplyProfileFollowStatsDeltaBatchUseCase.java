package dev.ngb.worker.profile.application;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.repository.ProfileStatsDeltaEventDedupeRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.event.ProfileFollowStatsDeltaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Applies {@link ProfileFollowStatsDeltaEvent} from a Kafka poll batch: dedupes by event uuid, aggregates per profile,
 * then issues atomic counter updates.
 */
@Slf4j
@RequiredArgsConstructor
public class ApplyProfileFollowStatsDeltaBatchUseCase implements UseCaseService {

    private final ObjectMapper objectMapper;
    private final ProfileStatsDeltaEventDedupeRepository dedupeRepository;
    private final ProfileStatsRepository profileStatsRepository;

    @Transactional
    public void execute(List<ConsumerRecord<String, String>> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<Long, Long> followerDeltas = new HashMap<>();
        Map<Long, Long> followingDeltas = new HashMap<>();

        for (ConsumerRecord<String, String> record : records) {
            String raw = record.value();
            if (raw == null) {
                continue;
            }
            ProfileFollowStatsDeltaEvent event = parse(raw);
            if (!dedupeRepository.tryClaimEvent(event.uuid())) {
                continue;
            }
            add(followerDeltas, event.targetProfileId(), event.followerDelta());
            add(followingDeltas, event.followerProfileId(), event.followingDelta());
        }

        for (Map.Entry<Long, Long> e : followerDeltas.entrySet()) {
            profileStatsRepository.adjustFollowerCount(e.getKey(), e.getValue());
        }
        for (Map.Entry<Long, Long> e : followingDeltas.entrySet()) {
            profileStatsRepository.adjustFollowingCount(e.getKey(), e.getValue());
        }
    }

    private static void add(Map<Long, Long> m, long profileId, int delta) {
        if (delta == 0) {
            return;
        }
        m.merge(profileId, (long) delta, Long::sum);
    }

    private ProfileFollowStatsDeltaEvent parse(String json) {
        try {
            return objectMapper.readValue(json, ProfileFollowStatsDeltaEvent.class);
        } catch (JacksonException ex) {
            throw new IllegalArgumentException("Invalid ProfileFollowStatsDeltaEvent JSON", ex);
        }
    }
}
