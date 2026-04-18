package dev.ngb.application.port;

import java.time.Instant;

public interface EventPublicationStore {

    void save(String type, String typeClazz, String payload, Instant occurredAt);
}
