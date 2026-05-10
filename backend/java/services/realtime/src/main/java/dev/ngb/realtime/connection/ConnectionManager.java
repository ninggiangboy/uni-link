package dev.ngb.realtime.connection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class ConnectionManager implements EmitterRegistry {

    private final ConcurrentMap<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<SseEmitter>> topicEmitters = new ConcurrentHashMap<>();

    @Override
    public void registerUser(String userId, SseEmitter emitter) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId cannot be blank");
        }
        if (emitter == null) {
            throw new IllegalArgumentException("emitter cannot be null");
        }

        log.debug("Registering SSE emitter for user: {}", userId);
        SseEmitter previous = userEmitters.put(userId, emitter);
        if (previous != null) {
            safeComplete(previous);
        }

        emitter.onCompletion(() -> {
            log.debug("SSE connection completed for user: {}", userId);
            userEmitters.remove(userId, emitter);
        });
        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out for user: {}", userId);
            userEmitters.remove(userId, emitter);
        });
        emitter.onError(ex -> {
            log.debug("SSE connection error for user: {}", userId, ex);
            userEmitters.remove(userId, emitter);
        });
    }

    @Override
    public void registerTopic(String topicId, SseEmitter emitter) {
        if (topicId == null || topicId.isBlank()) {
            throw new IllegalArgumentException("topicId cannot be blank");
        }
        if (emitter == null) {
            throw new IllegalArgumentException("emitter cannot be null");
        }

        log.debug("Registering SSE emitter for topic: {}", topicId);
        Set<SseEmitter> set = topicEmitters.computeIfAbsent(topicId, _ -> ConcurrentHashMap.newKeySet());
        set.add(emitter);

        Runnable cleanup = () -> removeTopicEmitter(topicId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());
    }

    @Override
    public SseEmitter getUserEmitter(String userId) {
        return userEmitters.get(userId);
    }

    @Override
    public Collection<SseEmitter> getTopicEmitters(String topicId) {
        return topicEmitters.getOrDefault(topicId, Set.of());
    }

    @Override
    public Collection<SseEmitter> getAllUserEmitters() {
        return userEmitters.values();
    }

    @Override
    public Iterable<? extends Iterable<SseEmitter>> getAllTopicEmitterSets() {
        return topicEmitters.values();
    }

    @Override
    public void removeUser(String userId) {
        log.debug("Removing SSE emitter for user: {}", userId);
        SseEmitter emitter = userEmitters.remove(userId);
        if (emitter != null) {
            safeComplete(emitter);
        }
    }

    @Override
    public void removeTopicEmitter(String topicId, SseEmitter emitter) {
        topicEmitters.computeIfPresent(topicId, (key, set) -> {
            set.remove(emitter);
            return set.isEmpty() ? null : set;
        });
    }

    private void safeComplete(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception ignored) {
        }
    }

    public void safeSend(SseEmitter emitter, Object data) throws IOException {
        emitter.send(SseEmitter.event().data(data));
    }
}
