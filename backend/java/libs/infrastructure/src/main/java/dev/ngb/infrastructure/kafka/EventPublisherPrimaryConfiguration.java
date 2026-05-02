package dev.ngb.infrastructure.kafka;

import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.infrastructure.kafka.cdc.producer.CdcKafkaEventPublisher;
import dev.ngb.infrastructure.kafka.simple.producer.SimpleKafkaEventPublisher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnBean(KafkaTemplate.class)
public class EventPublisherPrimaryConfiguration {

    /**
     * Prefers the outbox-backed publisher when both are registered so events survive process crash
     * before Kafka send; falls back to direct Kafka publish when CDC/outbox is not in use.
     */
    @Bean
    @Primary
    public EventPublisher primaryEventPublisher(
            ObjectProvider<CdcKafkaEventPublisher> cdcKafkaEventPublisher,
            ObjectProvider<SimpleKafkaEventPublisher> simpleKafkaEventPublisher
    ) {
        CdcKafkaEventPublisher cdc = cdcKafkaEventPublisher.getIfAvailable();
        if (cdc != null) {
            return cdc;
        }
        return simpleKafkaEventPublisher.getObject();
    }
}
