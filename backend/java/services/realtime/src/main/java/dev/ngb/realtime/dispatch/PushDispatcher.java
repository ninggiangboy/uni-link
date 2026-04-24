package dev.ngb.realtime.dispatch;

import dev.ngb.event.PushEvent;
import dev.ngb.realtime.connection.EmitterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushDispatcher {

    private final EmitterRegistry emitterRegistry;
    private final MessageBroker messageBroker;

    public void dispatch(PushEvent event) {
        if (event == null) {
            return;
        }
        switch (event.protocol()) {
            case SSE -> pushViaSse(event);
            case WS -> pushViaWs(event);
        }
    }

    private void pushViaSse(PushEvent event) {
        switch (event.targetType()) {
            case SPECIFIC -> pushSseToUser(event);
            case TOPIC -> pushSseToTopic(event);
            case ALL -> pushSseToAll(event);
        }
    }

    private void pushSseToUser(PushEvent event) {
        SseEmitter emitter = emitterRegistry.getUserEmitter(event.targetId());
        if (emitter != null) {
            sendSse(emitter, event.data());
        }
    }

    private void pushSseToTopic(PushEvent event) {
        Set<SseEmitter> emitters = Set.copyOf(emitterRegistry.getTopicEmitters(event.targetId()));
        for (SseEmitter emitter : emitters) {
            sendSse(emitter, event.data());
        }
    }

    private void pushSseToAll(PushEvent event) {
        for (SseEmitter emitter : emitterRegistry.getAllUserEmitters()) {
            sendSse(emitter, event.data());
        }
        for (var set : emitterRegistry.getAllTopicEmitterSets()) {
            for (SseEmitter emitter : set) {
                sendSse(emitter, event.data());
            }
        }
    }

    private void sendSse(SseEmitter emitter, Object data) {
        try {
            emitter.send(SseEmitter.event().data(data));
        } catch (IOException ex) {
            log.debug("Failed to send SSE event", ex);
            try {
                emitter.completeWithError(ex);
            } catch (Exception ignored) {
            }
        }
    }

    private void pushViaWs(PushEvent event) {
        String destination = event.destination();
        if (destination == null || destination.isBlank()) {
            destination = "/queue/push";
        }

        switch (event.targetType()) {
            case SPECIFIC -> messageBroker.sendToUser(event.targetId(), destination, event.data());
            case TOPIC -> messageBroker.sendToTopic(event.targetId(), destination, event.data());
            case ALL -> messageBroker.broadcast(destination, event.data());
        }
    }
}
