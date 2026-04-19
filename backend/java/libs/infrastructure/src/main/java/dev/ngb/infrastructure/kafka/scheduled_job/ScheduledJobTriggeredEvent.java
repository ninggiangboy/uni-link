package dev.ngb.infrastructure.kafka.scheduled_job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.f4b6a3.uuid.UuidCreator;
import dev.ngb.application.port.time.TimeProvider;
import dev.ngb.constant.TopicNames;
import dev.ngb.event.Event;
import dev.ngb.event.Topic;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Topic(TopicNames.SCHEDULED_JOB_TRIGGERED)
public record ScheduledJobTriggeredEvent(
        String eventId,
        String scheduledJobName,
        Instant occurredAt,
        @Nullable JsonNode payload
) implements Event {

    public static ScheduledJobTriggeredEvent create(
            String scheduledJobName,
            TimeProvider timeProvider,
            ObjectMapper objectMapper) {
        return create(scheduledJobName, null, timeProvider, objectMapper);
    }

    public static ScheduledJobTriggeredEvent create(
            String scheduledJobName,
            @Nullable JsonNode payload,
            TimeProvider timeProvider,
            ObjectMapper objectMapper) {
        return new ScheduledJobTriggeredEvent(
                UuidCreator.getTimeOrdered().toString(),
                scheduledJobName,
                timeProvider.now(),
                payload != null ? payload : objectMapper.createObjectNode());
    }
}
