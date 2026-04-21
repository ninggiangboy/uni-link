package dev.ngb.realtime.subscriber;

import dev.ngb.constant.TopicNames;
import dev.ngb.event.PushEvent;
import dev.ngb.realtime.dispatch.PushDispatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPushSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final PushDispatcher dispatcher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            PushEvent event = objectMapper.readValue(payload, PushEvent.class);
            dispatcher.dispatch(event);
        } catch (Exception ex) {
            log.warn("Failed to process PushEvent payload: {}", payload, ex);
        }
    }
}