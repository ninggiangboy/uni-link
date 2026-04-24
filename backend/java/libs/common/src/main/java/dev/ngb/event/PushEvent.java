package dev.ngb.event;

import java.time.Instant;

/**
 * Plain DTO for Redis Pub/Sub push messages.
 * <p>
 * This is intentionally not a Kafka-routed domain event (no {@link Topic}, does not implement {@link Event}).
 */
public record PushEvent(
        String targetId,
        TargetType targetType,
        Protocol protocol,
        String destination,
        Object data,
        Instant occurredAt
) {
    public enum TargetType { SPECIFIC, TOPIC, ALL }

    public enum Protocol { SSE, WS }
}

