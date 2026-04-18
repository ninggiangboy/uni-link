package dev.ngb.domain.attachment.model.attachment;

import dev.ngb.domain.DomainEntity;
import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import lombok.Getter;

import java.time.Instant;

/**
 * Binary attachment metadata for an account-owned object in external storage.
 */
@Getter
public class Attachment extends DomainEntity<Long> {

    private Long accountId;
    private String bucket;
    private String objectKey;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String fileUrl;
    private AttachmentUploadStatus uploadStatus;
    private String processingRequestId;
    private Instant processingRequestedAt;
    private Instant processedAt;

    private Attachment() {
    }

    public static Attachment createPendingPut(
            Long accountId,
            String bucket,
            String objectKey,
            String fileName,
            String contentType,
            Long sizeBytes,
            String fileUrl,
            Long createdBy
    ) {
        Attachment a = new Attachment();
        Instant now = Instant.now(a.clock);
        a.accountId = accountId;
        a.bucket = bucket;
        a.objectKey = objectKey;
        a.fileName = fileName;
        a.contentType = contentType;
        a.sizeBytes = sizeBytes;
        a.fileUrl = fileUrl;
        a.uploadStatus = AttachmentUploadStatus.PENDING_PUT;
        a.createdBy = createdBy;
        a.updatedBy = createdBy;
        a.createdAt = now;
        a.updatedAt = now;
        return a;
    }

    public static Attachment reconstruct(
            Long id,
            String uuid,
            Long createdBy,
            Instant createdAt,
            Long updatedBy,
            Instant updatedAt,
            Long accountId,
            String bucket,
            String objectKey,
            String fileName,
            String contentType,
            Long sizeBytes,
            String fileUrl,
            AttachmentUploadStatus uploadStatus,
            String processingRequestId,
            Instant processingRequestedAt,
            Instant processedAt
    ) {
        Attachment a = new Attachment();
        a.id = id;
        a.uuid = uuid;
        a.createdBy = createdBy;
        a.createdAt = createdAt;
        a.updatedBy = updatedBy;
        a.updatedAt = updatedAt;
        a.accountId = accountId;
        a.bucket = bucket;
        a.objectKey = objectKey;
        a.fileName = fileName;
        a.contentType = contentType;
        a.sizeBytes = sizeBytes;
        a.fileUrl = fileUrl;
        a.uploadStatus = uploadStatus;
        a.processingRequestId = processingRequestId;
        a.processingRequestedAt = processingRequestedAt;
        a.processedAt = processedAt;
        return a;
    }

    /**
     * Marks the attachment as successfully stored after upload confirmation.
     */
    public void markAvailable() {
        this.uploadStatus = AttachmentUploadStatus.AVAILABLE;
    }

    /**
     * Marks the attachment as abandoned or timed out (used by maintenance sweep).
     */
    public void markExpired() {
        this.uploadStatus = AttachmentUploadStatus.EXPIRED;
    }

    public boolean isProcessed() {
        return processedAt != null;
    }

    public boolean isProcessingRequested() {
        return processingRequestedAt != null;
    }

    public void markProcessingRequested(String requestId) {
        this.processingRequestId = requestId;
        this.processingRequestedAt = Instant.now(clock);
    }

    public boolean hasProcessingRequest(String requestId) {
        return requestId != null && requestId.equals(this.processingRequestId);
    }

    public void markProcessingFailed() {
        this.processingRequestId = null;
        this.processingRequestedAt = null;
    }

    public void markProcessedVariant(
            String bucket,
            String objectKey,
            String contentType,
            long sizeBytes,
            String fileUrl
    ) {
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.fileUrl = fileUrl;
        this.processingRequestId = null;
        this.processingRequestedAt = null;
        this.processedAt = Instant.now(clock);
    }
}
