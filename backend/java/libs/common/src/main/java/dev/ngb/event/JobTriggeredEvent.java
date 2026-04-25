package dev.ngb.event;

import dev.ngb.constant.TopicNames;

import java.time.Instant;
import java.util.UUID;

@Topic(TopicNames.JOB_TRIGGERED)
public record JobTriggeredEvent(
        String uuid,
        String scheduledJobName,
        Instant occurredAt,
        Object payload
) implements Event {

    public static JobTriggeredEvent create(String scheduledJobName) {
        return create(scheduledJobName, null);
    }

    public static JobTriggeredEvent create(String scheduledJobName, Object payload) {
        return new JobTriggeredEvent(
                UUID.randomUUID().toString(),
                scheduledJobName,
                Instant.now(),
                payload);
    }
}
