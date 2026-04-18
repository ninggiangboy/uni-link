package dev.ngb.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class RedissonRedisClient implements RedisClient {

    private final RedissonClient redissonClient;

    @Override
    public <T> T get(String key) {
        return redissonClient.<T>getBucket(key).get();
    }

    @Override
    public <T> void set(String key, T value) {
        redissonClient.<T>getBucket(key).set(value);
    }

    @Override
    public <T> void set(String key, T value, Duration ttl) {
        redissonClient.<T>getBucket(key).set(value, ttl);
    }

    @Override
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    @Override
    public void delete(List<String> keys) {
        redissonClient.getKeys().delete(keys.toArray(new String[0]));
    }

    @Override
    public Set<String> scan(String pattern, int count) {
        RKeys keys = redissonClient.getKeys();

        KeysScanOptions options = KeysScanOptions.defaults()
                .pattern(pattern)
                .chunkSize(count);

        Set<String> result = new HashSet<>();
        keys.getKeysStream(options).forEach(result::add);
        return result;
    }

    @Override
    public boolean tryLock(String key, long waitTime, TimeUnit unit) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        return lock.tryLock(waitTime, unit);
    }

    @Override
    public boolean isHeldByCurrentThread(String key) {
        return redissonClient.getLock(key).isHeldByCurrentThread();
    }

    @Override
    public void unlock(String key) {
        redissonClient.getLock(key).unlock();
    }
}
