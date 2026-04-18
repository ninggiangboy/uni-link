package dev.ngb.application.port;

import java.time.Instant;

public interface ProcessedEventStore {

    /**
     * @return true when the event is marked for the first time; false when it was already processed.
     */
    boolean markProcessed(String eventId, Instant processedAt);
}
