package dev.ngb.event;

import java.util.concurrent.ConcurrentHashMap;

public final class EventTopicResolver {

    private static final ConcurrentHashMap<Class<?>, String> CACHE = new ConcurrentHashMap<>();

    private EventTopicResolver() {}

    public static String resolve(Class<?> eventClass) {
        return CACHE.computeIfAbsent(eventClass, EventTopicResolver::resolveUncached);
    }

    private static String resolveUncached(Class<?> eventClass) {
        Topic annotation = eventClass.getAnnotation(Topic.class);
        if (annotation == null) {
            throw new IllegalArgumentException("No Topic information found on " + eventClass.getName());
        }
        return annotation.value();
    }
}
