package dev.ngb.domain.attachment.model;

/**
 * Lifecycle of a stored attachment relative to object storage.
 */
public enum AttachmentUploadStatus {

    /**
     * Presigned PUT was issued; bytes may not be present in the bucket yet.
     */
    PENDING_PUT,

    /**
     * Client confirmed upload (or sweep verified the object exists in storage).
     */
    AVAILABLE,

    /**
     * Presigned window elapsed without a successful upload confirmation; object may be absent.
     */
    EXPIRED
}
