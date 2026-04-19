package dev.ngb.scheduler.event;

import dev.ngb.event.Event;
import dev.ngb.event.EventTopicResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

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
        log.info("Publishing event topic: {} payload: {}", topic, payload);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, payload);
        record.headers().add(new RecordHeader("__TypeId__", eventClass.getName().getBytes(StandardCharsets.UTF_8)));
        kafkaTemplate.send(record);
    }
}
