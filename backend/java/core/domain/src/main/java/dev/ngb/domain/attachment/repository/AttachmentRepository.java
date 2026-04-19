package dev.ngb.domain.attachment.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Persistence for {@link Attachment} records.
 */
public interface AttachmentRepository extends Repository<Attachment, Long> {

    Optional<Attachment> findByUuidAndAccountId(String uuid, Long accountId);

    List<Attachment> findByUploadStatusAndCreatedAtBefore(AttachmentUploadStatus status, Instant createdBefore);

    List<Attachment> findByUploadStatusAndCreatedAtBefore(
            AttachmentUploadStatus status,
            Instant createdBefore,
            int limit
    );

    List<Attachment> findAvailableUnprocessedImages(int limit);

    Optional<Attachment> findByUuid(String uuid);
}
