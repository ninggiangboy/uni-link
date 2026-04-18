package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * Helpers for validating and normalizing user-supplied file names for storage keys and metadata.
 */
@UtilityClass
public final class FilenameUtils {

    public static final int DEFAULT_MAX_STORAGE_BASENAME_LENGTH = 180;

    /**
     * Display name as entered (trimmed) plus a basename safe for object keys after sanitization.
     *
     * @param displayName  trimmed original file name suitable for DB metadata
     * @param storageBasename sanitized segment used inside storage keys
     */
    public record ResolvedUploadFilename(String displayName, String storageBasename) {
    }

    /**
     * Trims the input and resolves a storage basename when the name is a single path segment.
     *
     * @param fileName raw file name from the client (may be null)
     * @param maxBasenameLength maximum length of the storage basename
     * @return empty when the name is blank, contains path separators, or sanitizes to empty
     */
    public static Optional<ResolvedUploadFilename> resolveForAttachmentUpload(
            String fileName,
            int maxBasenameLength
    ) {
        String display = trimToEmpty(fileName);
        if (display.isEmpty() || containsPathSeparator(display)) {
            return Optional.empty();
        }
        String storage = toSafeStorageBasename(display, maxBasenameLength);
        if (storage.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ResolvedUploadFilename(display, storage));
    }

    public static String trimToEmpty(String fileName) {
        return fileName == null ? "" : fileName.trim();
    }

    public static boolean containsPathSeparator(String name) {
        return name.indexOf('/') >= 0 || name.indexOf('\\') >= 0;
    }

    /**
     * Produces a single path segment safe for S3-style keys: alphanumerics, dot, underscore, hyphen;
     * other characters become underscores; then truncated.
     */
    public static String toSafeStorageBasename(String singleSegmentName, int maxLength) {
        if (singleSegmentName == null || singleSegmentName.isEmpty()) {
            return "";
        }
        String base = singleSegmentName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (base.length() > maxLength) {
            base = base.substring(0, maxLength);
        }
        return base;
    }
}
