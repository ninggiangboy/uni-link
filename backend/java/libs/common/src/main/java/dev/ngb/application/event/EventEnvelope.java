package dev.ngb.application.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EventEnvelope<T>(
        String id,
        String type,
        Instant occurredAt,
        T payload,
        Map<String, String> metadata
) {

    public static <T> EventEnvelope<T> of(String type, T payload) {
        return new EventEnvelope<>(
                UUID.randomUUID().toString(),
                type,
                Instant.now(),
                payload,
                Map.of()
        );
    }
}
