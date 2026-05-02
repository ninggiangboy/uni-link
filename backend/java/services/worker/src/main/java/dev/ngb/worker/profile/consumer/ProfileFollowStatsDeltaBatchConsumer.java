package dev.ngb.worker.profile.consumer;

import dev.ngb.constant.KafkaListenerContainerFactoryNames;
import dev.ngb.constant.TopicNames;
import dev.ngb.worker.profile.application.ApplyProfileFollowStatsDeltaBatchUseCase;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfileFollowStatsDeltaBatchConsumer {

    private final ApplyProfileFollowStatsDeltaBatchUseCase applyProfileFollowStatsDeltaBatchUseCase;

    @KafkaListener(
            topics = TopicNames.PROFILE_FOLLOW_STATS_DELTA,
            containerFactory = KafkaListenerContainerFactoryNames.PROFILE_FOLLOW_STATS_DELTA_BATCH
    )
    public void consume(List<ConsumerRecord<String, String>> records) {
        applyProfileFollowStatsDeltaBatchUseCase.execute(records);
    }
}
