package dev.ngb.application.port.cache;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface Cache {
    <V> void set(String key, V value);
    <V> void set(String key, V value, Duration ttl);
    <V> Optional<V> getOrCreate(String key, Supplier<V> loader);
    <V> Optional<V> getOrCreate(String key, Supplier<V> loader, Duration ttl);
    <V> Optional<V> get(String key);
    void remove(String key);
    void removeAll(List<String> keys);
    void removeByPrefix(String prefix);
}
