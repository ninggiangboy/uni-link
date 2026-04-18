package dev.ngb.infrastructure.redis.cache;

import dev.ngb.application.port.cache.DistributedCache;
import dev.ngb.infrastructure.redis.RedisClient;
import dev.ngb.util.RetryUtils;
import dev.ngb.util.StopwatchUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Redis-based distributed cache implementation.
 *
 * <p>
 * Key features:
 * <ul>
 *   <li>Distributed locking to prevent cache stampede across JVMs</li>
 *   <li>Local in-flight future cache to deduplicate concurrent loads per JVM</li>
 *   <li>Batch delete & scan to avoid blocking Redis</li>
 * </ul>
 */
@RequiredArgsConstructor
@Slf4j
public class RedisDistributedCache implements DistributedCache {

    private final RedisClient redisClient;

    /**
     * Local in-flight cache (per JVM).
     *
     * <p>
     * Prevents multiple threads in the same JVM from loading the same key
     * concurrently when a cache miss happens.
     */
    private final ConcurrentMap<String, CompletableFuture<Object>> futureCache = new ConcurrentHashMap<>();
    /**
     * Executor used for cache loading logic
     */
    private final Executor executor;

    /**
     * Max time to wait for distributed lock
     */
    private static final long LOCK_WAIT_TIME_MS = 2_000;

    private static final int CACHE_WAIT_RETRY = 3;
    private static final long CACHE_WAIT_BASE_MS = 50;

    private static final int DELETE_BATCH_SIZE = 200;
    private static final int SCAN_BATCH_SIZE = 500;

    private static final String CACHE_PREFIX = "cache:";
    private static final String LOCK_CACHE_PREFIX = "cache:lock:";

    @Override
    public <V> void set(String key, V value) {
        set(key, value, null);
    }

    @Override
    public <V> void set(String key, V value, Duration ttl) {
        if (key == null) {
            throw new IllegalArgumentException("Cache key cannot be null");
        }

        String realKey = buildCacheKey(key);

        if (ttl == null) {
            redisClient.set(realKey, value);
        } else {
            redisClient.set(realKey, value, ttl);
        }
    }

    @Override
    public <V> Optional<V> getOrCreate(String key, Supplier<V> loader) {
        return getOrCreate(key, loader, null);
    }

    /**
     * Get value from cache or load it if missing.
     *
     * <p>
     * Flow:
     * <ol>
     *   <li>Try Redis cache</li>
     *   <li>Deduplicate concurrent loaders via local future cache</li>
     *   <li>Use Redis lock to prevent cross-JVM stampede</li>
     * </ol>
     */
    @Override
    public <V> Optional<V> getOrCreate(String key, Supplier<V> loader, Duration ttl) {
        String realKey = buildCacheKey(key);

        // Fast path: cache hit
        V cached = redisClient.get(realKey);

        if (cached != null || loader == null) {
            return Optional.ofNullable(cached);
        }

        String lockKey = buildLockKey(key);

        CompletableFuture<Object> future = futureCache.computeIfAbsent(
                realKey,
                k -> {
                    CompletableFuture<Object> f = CompletableFuture.supplyAsync(
                            () -> loadWithLock(realKey, key, lockKey, loader, ttl),
                            executor
                    );
                    // Ensure cleanup of local in-flight cache
                    f.whenComplete((_, _) -> futureCache.remove(k, f));
                    return f;
                });

        try {
            @SuppressWarnings("unchecked")
            V value = (V) future.get();
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Failed loading cache key {}", realKey, e);
            return Optional.empty();
        }
    }

    /**
     * Load value with distributed lock protection.
     *
     * <p>
     * Only one node is allowed to load the data.
     * Others will briefly wait and retry Redis.
     *
     * <p>
     * Lock uses Redisson's Watchdog mechanism for auto-renewal,
     * with a fallback lease time in case watchdog fails.
     */
    private <V> V loadWithLock(
            String realKey,
            String key,
            String lockKey,
            Supplier<V> loader,
            Duration ttl
    ) {

        boolean locked = false;

        try {
            // enables Redisson Watchdog auto-renewal
            locked = redisClient.tryLock(lockKey, LOCK_WAIT_TIME_MS, TimeUnit.MILLISECONDS);

            // Double-check cache after attempting lock
            V value = redisClient.get(realKey);
            if (value != null) {
                return value;
            }

            // Failed to acquire lock → wait for another node to populate cache
            if (!locked) {
                log.debug("Failed to acquire lock for key {}, waiting for cache population", key);
                value = waitForCache(realKey);

                // If still no value after waiting, try to load anyway
                if (value == null) {
                    log.warn("Cache still empty after waiting for key {}, attempting to load", key);
                    value = loadValue(key, loader, ttl);
                }
                return value;
            }

            // We have the lock, load the value
            log.warn("Cache miss for key {}, loading...", key);
            return loadValue(key, loader, ttl);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CompletionException("Interrupted while acquiring lock", e);
        } catch (Exception e) {
            throw new CompletionException("Failed to load cache with lock", e);
        } finally {
            if (locked && redisClient.isHeldByCurrentThread(lockKey)) {
                try {
                    redisClient.unlock(lockKey);
                } catch (Exception e) {
                    log.error("Failed to unlock for key {}", lockKey, e);
                }
            }
        }
    }

    /**
     * Load value from supplier and store in cache.
     */
    private <V> V loadValue(String key, Supplier<V> loader, Duration ttl) {
        V value = StopwatchUtils.measure(
                loader,
                cost -> log.debug("Cache miss for key {}, loaded in {} ms", key, cost)
        );

        if (value != null) {
            try {
                set(key, value, ttl);
            } catch (Exception e) {
                log.error("Failed to store value in cache for key {}", key, e);
                // Continue and return the loaded value even if caching fails
            }
        }

        return value;
    }

    /**
     * Small retry loop to wait for cache population by another node.
     */
    private <V> V waitForCache(String realKey) {
        try {
            return RetryUtils.retry(
                    CACHE_WAIT_RETRY,
                    CACHE_WAIT_BASE_MS,
                    CACHE_WAIT_BASE_MS * CACHE_WAIT_RETRY,
                    NoSuchElementException.class,
                    () -> {
                        V value = redisClient.get(realKey);
                        if (value == null) {
                            throw new NoSuchElementException("Cache not ready");
                        }
                        return value;
                    }
            );
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public <V> Optional<V> get(String key) {
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                redisClient.get(buildCacheKey(key))
        );
    }

    @Override
    public void remove(String key) {
        String realKey = buildCacheKey(key);
        futureCache.remove(realKey);
        redisClient.delete(realKey);
    }

    /**
     * Remove multiple keys in batches to avoid Redis overload.
     */
    @Override
    public void removeAll(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<String> realKeys = new ArrayList<>(keys.size());
        for (String key : keys) {
            String realKey = buildCacheKey(key);
            realKeys.add(realKey);
            futureCache.remove(realKey);
        }

        for (int i = 0; i < realKeys.size(); i += DELETE_BATCH_SIZE) {
            int end = Math.min(i + DELETE_BATCH_SIZE, realKeys.size());
            List<String> batchKeys = realKeys.subList(i, end);

            try {
                redisClient.delete(batchKeys);
            } catch (Exception e) {
                log.error("Failed to delete cache batch {}", batchKeys, e);
            }
        }
    }

    /**
     * Remove all cache entries matching a given prefix.
     *
     * <p>
     * Uses SCAN instead of KEYS to avoid blocking Redis.
     */
    @Override
    public void removeByPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return;
        }

        String realPrefix = CACHE_PREFIX + prefix;

        Set<String> keys = redisClient.scan(realPrefix + "*", SCAN_BATCH_SIZE);

        List<String> batch = new ArrayList<>(DELETE_BATCH_SIZE);

        for (String key : keys) {
            batch.add(key);
            futureCache.remove(key);

            if (batch.size() >= DELETE_BATCH_SIZE) {
                redisClient.delete(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            redisClient.delete(batch);
        }
    }

    private String buildCacheKey(String key) {
        return CACHE_PREFIX + key;
    }

    private String buildLockKey(String key) {
        return LOCK_CACHE_PREFIX + key;
    }
}
