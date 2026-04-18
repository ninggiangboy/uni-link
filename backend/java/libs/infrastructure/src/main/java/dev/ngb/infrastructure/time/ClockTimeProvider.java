package dev.ngb.infrastructure.time;

import dev.ngb.application.port.time.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ClockTimeProvider implements TimeProvider {

    private final Clock clock;

    @Override
    public long nowMillis() {
        return clock.millis();
    }

    @Override
    public Instant now() {
        return clock.instant();
    }
}
