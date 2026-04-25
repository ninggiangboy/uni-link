package dev.ngb.infrastructure.kafka.simple.producer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.event.Event;
import dev.ngb.event.EventTopicResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleKafkaEventPublisher implements EventPublisher {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<@NonNull String, @NonNull String> kafkaTemplate;

    @Override
    public void publish(Event event) {
        Class<?> eventClass = event.getClass();
        String topic = EventTopicResolver.resolve(eventClass);
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Failed to serialize event: " + event.getClass().getName(), ex);
        }
        log.info("Publishing event topic: {} type: {}", topic, eventClass.getSimpleName());
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, event.uuid(), payload);
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
