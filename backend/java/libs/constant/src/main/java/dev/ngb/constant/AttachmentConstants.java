package dev.ngb.constant;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Fixed policy values for presigned attachment uploads (not loaded from configuration).
 */
@UtilityClass
public final class AttachmentConstants {

    public static final long PRESIGN_TTL_SECONDS = 300L;

    public static final long MIN_PRESIGN_TTL_SECONDS = 60L;

    /**
     * Extra time after presign TTL before a {@code PENDING_PUT} row is considered stale for sweep processing.
     */
    public static final long PENDING_STALE_GRACE_SECONDS = 300L;

    /**
     * Maximum declared size in bytes; {@code 0} disables max-size checks (and declared-size requirement).
     */
    public static final long MAX_ATTACHMENT_SIZE_BYTES = 52_428_800L;

    /**
     * Empty list means any non-blank content type is accepted; otherwise the type must match one entry.
     */
    public static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/png", "application/pdf");
}
