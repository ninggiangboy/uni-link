package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Locale;

/**
 * Helpers for HTTP content type checks used by upload-style flows.
 */
@UtilityClass
public final class ContentTypeUtils {

    /**
     * When {@code allowedContentTypes} is null or empty, any non-blank type is accepted.
     * Otherwise the type must match one entry (case-insensitive, trimmed).
     */
    public static boolean isAllowedContentType(String contentType, List<String> allowedContentTypes) {
        if (allowedContentTypes == null || allowedContentTypes.isEmpty()) {
            return true;
        }
        String normalized = contentType.toLowerCase(Locale.ROOT);
        return allowedContentTypes.stream()
                .anyMatch(ct -> ct != null && ct.trim().equalsIgnoreCase(normalized));
    }
}
