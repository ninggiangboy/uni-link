package dev.ngb.realtime.connection;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;

public interface EmitterRegistry {

    void registerUser(String userId, SseEmitter emitter);

    void registerTopic(String topicId, SseEmitter emitter);

    SseEmitter getUserEmitter(String userId);

    Collection<SseEmitter> getTopicEmitters(String topicId);

    Collection<SseEmitter> getAllUserEmitters();

    Iterable<? extends Iterable<SseEmitter>> getAllTopicEmitterSets();

    void removeUser(String userId);

    void removeTopicEmitter(String topicId, SseEmitter emitter);
}
