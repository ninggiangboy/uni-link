package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public final class BackoffUtils {

    private static final ThreadLocalRandom RND = ThreadLocalRandom.current();

    public static long fullJitter(
            long baseMs,
            int attempt,
            long maxMs
    ) {
        long exp = Math.min(maxMs, baseMs * (1L << attempt));
        return RND.nextLong(exp + 1);
    }

    public static long equalJitter(long backoffMs) {
        return backoffMs / 2 + RND.nextLong(backoffMs / 2 + 1);
    }

    public static long decorrelatedJitter(
            long baseMs,
            long prevMs,
            long maxMs
    ) {
        long min = baseMs;
        long max = Math.min(maxMs, prevMs * 3);
        return RND.nextLong(min, max + 1);
    }
}
