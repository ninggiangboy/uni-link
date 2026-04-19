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

    /**
     * Stale {@code PENDING_PUT} rows with {@code created_at} before the cutoff, using a stable id cursor for paging.
     * Rows are ordered by ascending id; the next page starts strictly after {@code idAfter} (use {@code 0} for the first page).
     */
    List<Attachment> findPendingPutStaleAfterId(Instant createdBefore, long idAfter, int limit);

    Optional<Attachment> findByUuid(String uuid);
}
