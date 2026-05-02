package dev.ngb.worker.consumer;

import dev.ngb.constant.KafkaListenerContainerFactoryNames;
import dev.ngb.constant.TopicNames;
import dev.ngb.worker.shared.public_api.ProfilePublicApi;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class ProfileFollowStatsDeltaBatchConsumer {

    private final ProfilePublicApi profilePublicApi;

    @KafkaListener(
            topics = TopicNames.PROFILE_FOLLOW_STATS_DELTA,
            containerFactory = KafkaListenerContainerFactoryNames.PROFILE_FOLLOW_STATS_DELTA_BATCH
    )
    public void consume(List<ConsumerRecord<String, String>> records) {
        List<String> payloads = records.stream()
                .map(ConsumerRecord::value)
                .collect(Collectors.toList());
        profilePublicApi.executeApplyProfileFollowStatsDeltaBatchUseCase(payloads);
    }
}
