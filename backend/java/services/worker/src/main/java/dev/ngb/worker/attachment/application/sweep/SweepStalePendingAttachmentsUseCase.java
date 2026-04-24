package dev.ngb.worker.attachment.application.sweep;

import dev.ngb.application.UseCaseService;
import dev.ngb.worker.attachment.public_api.SweepStalePendingAttachmentsPublicApi;
import dev.ngb.application.port.storage.ObjectStorage;
import dev.ngb.constant.AttachmentConstants;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import dev.ngb.util.batching.BatchExecutorUtils;
import dev.ngb.util.batching.core.ItemProcessor;
import dev.ngb.util.batching.core.ItemReader;
import dev.ngb.util.batching.core.ItemWriter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Marks stale {@code PENDING_PUT} attachments as {@code AVAILABLE} when the object exists, or {@code EXPIRED} when it
 * does not, after presign TTL plus a grace window.
 */
@Slf4j
@RequiredArgsConstructor
public class SweepStalePendingAttachmentsUseCase implements UseCaseService, SweepStalePendingAttachmentsPublicApi {

    /** Tuned for S3 exists + batched saves rather than {@code BatchExecutorUtils.executeIOBound} defaults. */
    private static final int PIPELINE_SHARDS = 1;
    private static final int PIPELINE_BUFFERED_ITEMS = 5_000;
    private static final int PIPELINE_WRITE_BATCH_SIZE = 200;
    private static final int PIPELINE_PROCESSOR_CONCURRENCY =
            Math.max(Runtime.getRuntime().availableProcessors() * 2, 16);
    private static final int PIPELINE_MAX_INFLIGHT = 8_000;

    private final AttachmentRepository attachmentRepository;
    private final ObjectStorage objectStorage;

    public void execute() {
        Instant startedAt = Instant.now();
        Instant cutoff = Instant.now().minusSeconds(
                AttachmentConstants.PRESIGN_TTL_SECONDS
                        + AttachmentConstants.PENDING_STALE_GRACE_SECONDS
        );

        AtomicLong totalFailed = new AtomicLong(0L);

        AttachmentItemReader reader = new AttachmentItemReader(attachmentRepository, cutoff);
        ItemProcessor<Attachment, Attachment> processor =
                attachment -> classifyAttachment(attachment, totalFailed);
        ItemWriter<Attachment> writer = attachmentRepository::saveAll;

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
            throw new IllegalStateException("Pending attachment sweep pipeline failed", ex);
        }

        long failed = totalFailed.get();
        long fetched = reader.getTotalFetched();
        long updated = fetched - failed;

        log.info(
                "Pending attachment sweep completed cutoff={} chunks={} fetched={} updated={} failed={} durationMs={}",
                cutoff,
                reader.getChunksLoaded(),
                fetched,
                updated,
                failed,
                Duration.between(startedAt, Instant.now()).toMillis()
        );
    }

    private Attachment classifyAttachment(Attachment attachment, AtomicLong totalFailed) {
        try {
            if (objectStorage.objectExists(attachment.getType().getBucket(), attachment.getObjectKey())) {
                attachment.markAvailable();
            } else {
                attachment.markExpired();
            }
            return attachment;
        } catch (RuntimeException ex) {
            log.warn(
                    "Pending attachment sweep failed attachmentId={} uuid={}",
                    attachment.getId(),
                    attachment.getUuid(),
                    ex
            );
            totalFailed.incrementAndGet();
            return null;
        }
    }

    @RequiredArgsConstructor
    public static class AttachmentItemReader implements ItemReader<Attachment> {

        private static final int BATCH_SIZE = 1_000;

        private final AttachmentRepository attachmentRepository;
        private final Instant cutoff;

        private Iterator<Attachment> iterator = Collections.emptyIterator();
        private boolean finished = false;
        /** Cursor for stable paging: the next query uses strictly greater ids (0 = first page). */
        private long lastId = 0L;

        @Getter
        private long totalFetched;
        @Getter
        private int chunksLoaded;

        @Override
        public Attachment read(int shardId, int totalShards) {
            if (finished) {
                return null;
            }

            if (!iterator.hasNext()) {
                List<Attachment> staleChunk = fetchNextChunk();

                if (staleChunk.isEmpty()) {
                    finished = true;
                    return null;
                }

                iterator = staleChunk.iterator();
            }

            return iterator.next();
        }

        private List<Attachment> fetchNextChunk() {
            List<Attachment> staleChunk = attachmentRepository.findPendingPutStaleAfterId(cutoff, lastId, BATCH_SIZE);

            if (!staleChunk.isEmpty()) {
                chunksLoaded++;
                totalFetched += staleChunk.size();
                lastId = staleChunk.stream().mapToLong(Attachment::getId).max().orElse(lastId);
            }

            return staleChunk;
        }
    }
}
