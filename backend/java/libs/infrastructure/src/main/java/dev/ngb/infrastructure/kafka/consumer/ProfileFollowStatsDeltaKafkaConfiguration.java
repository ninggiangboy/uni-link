package dev.ngb.infrastructure.kafka.consumer;

import dev.ngb.constant.KafkaListenerContainerFactoryNames;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
@EnableKafka
@ConditionalOnBean(KafkaTemplate.class)
public class ProfileFollowStatsDeltaKafkaConfiguration {

    @Bean(name = KafkaListenerContainerFactoryNames.PROFILE_FOLLOW_STATS_DELTA_BATCH)
    public ConcurrentKafkaListenerContainerFactory<String, String> profileFollowStatsDeltaBatchFactory(
            ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        return factory;
    }
}
