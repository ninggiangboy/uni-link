package dev.ngb.worker.consumer;

import dev.ngb.constant.ScheduledJobNames;
import dev.ngb.constant.TopicNames;
import dev.ngb.infrastructure.jdbc.event.entity.EventPublicationEntity;
import dev.ngb.event.JobTriggeredEvent;
import dev.ngb.worker.shared.public_api.AttachmentJobHandlers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobTriggeredConsumer {

    private final ObjectMapper objectMapper;
    private final AttachmentJobHandlers attachmentJobHandlers;

    @KafkaListener(topics = TopicNames.JOB_TRIGGERED)
    public void consume(String message) {
        JobTriggeredEvent scheduledJob = parse(message);
        String name = scheduledJob.scheduledJobName();
        switch (name) {
            case ScheduledJobNames.ATTACHMENT_PENDING_PUT_SWEEP -> attachmentJobHandlers.executeSweepStalePendingAttachmentsUseCase();
            case null, default -> log.debug("Unrecognized scheduled job, skipping: {}", name);
        }
    }

    private JobTriggeredEvent parse(String message) {
        try {
            EventPublicationEntity publication = objectMapper.readValue(message, EventPublicationEntity.class);
            return objectMapper.readValue(publication.getPayload(), JobTriggeredEvent.class);
        } catch (JacksonException ex) {
            throw new IllegalArgumentException("Invalid scheduled_job.triggered JSON", ex);
        }
    }
}
