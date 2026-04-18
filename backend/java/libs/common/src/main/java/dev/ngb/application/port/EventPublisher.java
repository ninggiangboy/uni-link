package dev.ngb.application.port;

import dev.ngb.application.event.EventEnvelope;

public interface EventPublisher {
    void publish(EventEnvelope<?> eventEnvelope);
}
