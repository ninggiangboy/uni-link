package dev.ngb.realtime.heartbeat;

import dev.ngb.realtime.connection.EmitterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseHeartbeatScheduler {

    private final EmitterRegistry emitterRegistry;

    @Scheduled(fixedRate = 15_000)
    public void heartbeat() {
        for (SseEmitter emitter : emitterRegistry.getAllUserEmitters()) {
            sendHeartbeat(emitter);
        }
        for (var set : emitterRegistry.getAllTopicEmitterSets()) {
            for (SseEmitter emitter : set) {
                sendHeartbeat(emitter);
            }
        }
    }

    private void sendHeartbeat(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().comment("heartbeat"));
        } catch (IOException ex) {
            try {
                emitter.completeWithError(ex);
            } catch (Exception ignored) {
            }
        }
    }
}
