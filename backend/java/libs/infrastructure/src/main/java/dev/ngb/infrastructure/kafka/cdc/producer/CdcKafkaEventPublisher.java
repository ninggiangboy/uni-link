package dev.ngb.infrastructure.kafka.cdc.producer;

import dev.ngb.event.Event;
import dev.ngb.event.EventTopicResolver;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.constant.TopicNames;
import dev.ngb.infrastructure.jdbc.event.entity.EventPublicationEntity;
import dev.ngb.infrastructure.jdbc.event.repository.EventPublicationRepository;
import dev.ngb.infrastructure.kafka.cdc.payload.CdcEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class CdcKafkaEventPublisher implements EventPublisher {

    private final EventPublicationRepository eventPublicationRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<@NonNull String, @NonNull String> kafkaTemplate;

    @Override
    public void publish(Event event) {
        String topicName = EventTopicResolver.resolve(event.getClass());
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Failed to serialize event: " + event.getClass().getName(), ex);
        }
        log.info("Saving outbox event: {}", payload);
        EventPublicationEntity eventPublication = EventPublicationEntity.builder()
                .type(topicName)
                .typeClazz(event.getClass().getName())
                .payload(payload)
                .occurredAt(event.occurredAt())
                .build();
        eventPublicationRepository.save(eventPublication);
    }

    @KafkaListener(topics = TopicNames.OUTBOX_EVENTS)
    public void consume(CdcEventPayload<EventPublicationEntity> cdcPayload) throws ClassNotFoundException {
        log.info("Received CDC event: {}", cdcPayload);
        if (cdcPayload.isEvent()) {
            EventPublicationEntity event = cdcPayload.payload().after();
            Class<?> eventClass = Class.forName(event.getTypeClazz());
            String topic = EventTopicResolver.resolve(eventClass);
            log.info("Publishing event topic: {}, payload: {}", topic, event.getPayload());
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.getUuid(), event.getPayload());
            record.headers().add(new RecordHeader("__TypeId__", eventClass.getName().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send event to topic {}", topic, ex);
                } else {
                    log.debug("Sent event to topic {} partition {} offset {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        }
    }
}
