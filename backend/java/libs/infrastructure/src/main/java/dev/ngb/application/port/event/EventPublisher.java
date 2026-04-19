package dev.ngb.application.port.event;

import dev.ngb.event.Event;

/**
 * Publishes {@link Event events} to interested subscribers.
 * <p>
 * Implementations are responsible for delivering events to one or more
 * listeners, either synchronously or asynchronously, depending on the
 * chosen messaging strategy.
 */
public interface EventPublisher {
    void publish(Event event);
}
