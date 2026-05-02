package dev.ngb.worker.consumer.config;

import dev.ngb.constant.KafkaListenerContainerFactoryNames;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
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
