package dev.ngb.infrastructure.redis.push;

import dev.ngb.constant.TopicNames;
import dev.ngb.event.PushEvent;
import dev.ngb.event.PushEventPublisher;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPushEventPublisher implements PushEventPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void push(PushEvent event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Failed to serialize PushEvent", ex);
        }
        redisTemplate.convertAndSend(TopicNames.REALTIME_PUSH_CHANNEL, payload);
    }
}

