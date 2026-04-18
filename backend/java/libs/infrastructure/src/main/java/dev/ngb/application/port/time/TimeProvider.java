package dev.ngb.application.port.time;

import java.time.Instant;

public interface TimeProvider {

    long nowMillis();

    Instant now();
}
