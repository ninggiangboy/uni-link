package dev.ngb.realtime.subscriber.controller;

import dev.ngb.realtime.connection.EmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final EmitterRegistry emitterRegistry;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();
        SseEmitter emitter = new SseEmitter(0L);
        emitterRegistry.registerUser(userId, emitter);
        return emitter;
    }

    @GetMapping(value = "/topic/{topicId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter topic(@PathVariable String topicId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitterRegistry.registerTopic(topicId, emitter);
        return emitter;
    }
}
