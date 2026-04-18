package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@UtilityClass
public final class TimeProvider {

    private static final ThreadLocal<Clock> CLOCK = ThreadLocal.withInitial(Clock::systemDefaultZone);

    public static Instant now() {
        return Instant.now(CLOCK.get());
    }

    public static long currentTimeMillis() {
        return CLOCK.get().millis();
    }

    public static void setFixedTime(Instant fixedInstant) {
        CLOCK.set(Clock.fixed(fixedInstant, ZoneId.systemDefault()));
    }

    public static void setOffsetTime(long offsetMillis) {
        CLOCK.set(Clock.offset(Clock.systemDefaultZone(), Duration.ofMillis(offsetMillis)));
    }

    public static void reset() {
        CLOCK.remove();
    }
}
