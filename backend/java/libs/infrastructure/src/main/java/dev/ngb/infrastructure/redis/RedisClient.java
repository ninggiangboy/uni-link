package dev.ngb.infrastructure.redis;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisClient {
    <T> T get(String key);

    <T> void set(String key, T value);

    <T> void set(String key, T value, Duration ttl);

    void delete(String key);

    void delete(List<String> keys);

    Set<String> scan(String pattern, int count);

    boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException;

    boolean isHeldByCurrentThread(String key);

    void unlock(String key);
}
