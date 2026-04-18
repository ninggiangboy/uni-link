package dev.ngb.app.support;

import java.util.UUID;

/**
 * Stable, unique values for integration tests (emails, etc.).
 */
public final class TestUtils {

    private TestUtils() {}

    public static String getUniqueEmail() {
        return "it-" + UUID.randomUUID() + "@integration.test";
    }
}
