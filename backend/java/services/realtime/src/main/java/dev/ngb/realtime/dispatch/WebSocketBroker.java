package dev.ngb.realtime.dispatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketBroker implements MessageBroker {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendToUser(String userId, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(userId, destination, payload);
    }

    @Override
    public void sendToTopic(String topic, String destination, Object payload) {
        messagingTemplate.convertAndSend("/topic/" + topic + destination, payload);
    }

    @Override
    public void broadcast(String destination, Object payload) {
        messagingTemplate.convertAndSend("/topic/broadcast" + destination, payload);
    }
}
