package dev.ngb.worker.attachment.application.sweep;

import dev.ngb.application.UseCaseService;
import dev.ngb.application.port.storage.ObjectStorage;
import dev.ngb.constant.AttachmentConstants;
import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Marks stale {@code PENDING_PUT} attachments as {@code AVAILABLE} when the object exists, or {@code EXPIRED} when it
 * does not, after presign TTL plus a grace window.
 */
@Slf4j
@RequiredArgsConstructor
public class SweepStalePendingAttachmentsUseCase implements UseCaseService {

    private final AttachmentRepository attachmentRepository;
    private final ObjectStorage objectStorage;

    public void execute() {
        Instant startedAt = Instant.now();
        Instant cutoff = Instant.now().minusSeconds(
                AttachmentConstants.PRESIGN_TTL_SECONDS
                        + AttachmentConstants.PENDING_STALE_GRACE_SECONDS
        );

        AtomicInteger failuresInCurrentChunk = new AtomicInteger(0);
        AtomicLong totalFailed = new AtomicLong(0L);

        IteratorItemReader reader = new IteratorItemReader(attachmentRepository, cutoff, failuresInCurrentChunk);
        ItemProcessor<Attachment, Attachment> processor =
                attachment -> classifyAttachment(attachment, failuresInCurrentChunk, totalFailed);
        ItemWriter<Attachment> writer = attachmentRepository::saveAll;

        try {
            BatchExecutorUtils.executeIOBound(reader, processor, writer);
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

    private Attachment classifyAttachment(
            Attachment attachment,
            AtomicInteger failuresInCurrentChunk,
            AtomicLong totalFailed
    ) {
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
            failuresInCurrentChunk.incrementAndGet();
            totalFailed.incrementAndGet();
            return null;
        }
    }

    @RequiredArgsConstructor
    public static class IteratorItemReader implements ItemReader<Attachment> {

        private static final int batchSize = 5000;

        private final AttachmentRepository attachmentRepository;
        private final Instant cutoff;
        /** Failures in the chunk currently being iterated; reset at each new fetch. */
        private final AtomicInteger failuresInCurrentChunk;

        private Iterator<Attachment> iterator = Collections.emptyIterator();
        private boolean finished = false;
        private int lastChunkSize = 0;

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
            if (lastChunkSize > 0 && failuresInCurrentChunk.get() >= lastChunkSize) {
                finished = true;
                return List.of();
            }

            failuresInCurrentChunk.set(0);

            List<Attachment> staleChunk = attachmentRepository.findByUploadStatusAndCreatedAtBefore(
                    AttachmentUploadStatus.PENDING_PUT,
                    cutoff,
                    batchSize
            );

            lastChunkSize = staleChunk.size();
            if (!staleChunk.isEmpty()) {
                chunksLoaded++;
                totalFetched += staleChunk.size();
            }

            return staleChunk;
        }
    }
}
