package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public final class TimeUtils {

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while sleeping", e);
        }
    }

    public static void sleep(Duration duration) {
        sleep(duration.toMillis());
    }
}
