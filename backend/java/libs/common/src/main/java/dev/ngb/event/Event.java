package dev.ngb.event;

import java.time.Instant;

/**
 * Marker interface representing an application event.
 * <p>
 * An event represents something that has already occurred in the system.
 * Events are immutable and are typically used to notify other parts of
 * the application or external systems about state changes.
 */
public interface Event {
    Instant occurredAt();
}
