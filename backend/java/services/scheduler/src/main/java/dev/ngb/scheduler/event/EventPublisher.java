package dev.ngb.scheduler.event;

import dev.ngb.event.Event;

public interface EventPublisher {
    void publish(Event event);
}
