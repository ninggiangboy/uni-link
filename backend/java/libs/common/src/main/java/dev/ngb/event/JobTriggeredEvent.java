package dev.ngb.event;

import dev.ngb.constant.TopicNames;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

import java.time.Instant;

@Topic(TopicNames.JOB_TRIGGERED)
public record JobTriggeredEvent(
        String scheduledJobName,
        Instant occurredAt,
        @Nullable JsonNode payload
) implements Event {

    public static JobTriggeredEvent create(String scheduledJobName) {
        return create(scheduledJobName, null);
    }

    public static JobTriggeredEvent create(String scheduledJobName, @Nullable JsonNode payload) {
        return new JobTriggeredEvent(
                scheduledJobName,
                Instant.now(),
                payload);
    }
}
