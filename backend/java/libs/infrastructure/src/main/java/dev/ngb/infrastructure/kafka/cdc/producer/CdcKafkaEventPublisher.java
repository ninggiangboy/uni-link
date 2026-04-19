package dev.ngb.infrastructure.kafka.cdc.producer;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.ngb.event.Event;
import dev.ngb.event.Topic;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.constant.TopicNames;
import dev.ngb.infrastructure.jdbc.event.entity.EventPublicationEntity;
import dev.ngb.infrastructure.jdbc.event.repository.EventPublicationRepository;
import dev.ngb.infrastructure.kafka.cdc.payload.CdcEventPayload;
import dev.ngb.util.TimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdcKafkaEventPublisher implements EventPublisher {

    private final EventPublicationRepository eventPublicationRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<@NonNull String, @NonNull Object> kafkaTemplate;

    @Override
    public void publish(Event event) {
        Topic topicAnn = event.getClass().getAnnotation(Topic.class);
        if (topicAnn == null) {
            throw new IllegalArgumentException("No Topic information found on " + event.getClass().getName());
        }
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Failed to serialize event: " + event.getClass().getName(), ex);
        }
        log.info("Saving outbox event: {}", payload);
        EventPublicationEntity eventPublication = EventPublicationEntity.builder()
                .id(UuidCreator.getTimeOrdered())
                .type(topicAnn.value())
                .typeClazz(event.getClass().getName())
                .payload(payload)
                .occurredAt(event.occurredAt())
                .createdAt(TimeProvider.now())
                .build();
        eventPublicationRepository.save(eventPublication);
    }

    @KafkaListener(topics = TopicNames.OUTBOX_EVENTS)
    public void consume(CdcEventPayload<EventPublicationEntity> cdcPayload) throws ClassNotFoundException {
        log.info("Received CDC event: {}", cdcPayload);
        if (cdcPayload.isCreate() && cdcPayload.payload().after() != null) {
            EventPublicationEntity event = cdcPayload.payload().after();
            Class<?> eventClass = Class.forName(event.getTypeClazz());
            Topic annotation = eventClass.getAnnotation(Topic.class);
            if (annotation == null) {
                throw new IllegalArgumentException("No Topic information found on " + event.getTypeClazz());
            }
            String topic = annotation.value();
            log.info("Publishing event topic: {}, payload: {}", topic, event.getPayload());
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, event.getId().toString(), event);
            record.headers().add(new RecordHeader("__TypeId__", eventClass.getName().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record);
        }
    }
}
