package dev.ngb.util.thread;

import dev.ngb.util.ExceptionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

@SuppressWarnings("preview")
public final class Parallel<K, T> {

    private final Map<K, ? extends Supplier<? extends T>> suppliers;
    private final Duration timeout;
    private final boolean failFast;

    private Parallel(
            Map<K, ? extends Supplier<? extends T>> suppliers,
            Duration timeout,
            boolean failFast) {

        this.suppliers = Collections.unmodifiableMap(new LinkedHashMap<>(suppliers));
        this.timeout = timeout;
        this.failFast = failFast;
    }

    @SafeVarargs
    public static <T> Parallel<Integer, T> of(Supplier<? extends T>... tasks) {
        return Parallel.of(List.of(tasks), null, true);
    }

    public static <T> Parallel<Integer, T> of(
            Collection<? extends Supplier<? extends T>> tasks,
            Duration timeout,
            boolean failFast) {

        Map<Integer, Supplier<? extends T>> indexed = new LinkedHashMap<>();

        int i = 0;
        for (var task : tasks) {
            indexed.put(i++, task);
        }

        return new Parallel<>(indexed, timeout, failFast);
    }

    public static <K, T> Parallel<K, T> of(Map<K, ? extends Supplier<? extends T>> tasks) {
        return new Parallel<>(tasks, null, true);
    }

    public static <K, T> Parallel<K, T> of(
            Map<K, ? extends Supplier<? extends T>> tasks,
            Duration timeout,
            boolean failFast) {

        return new Parallel<>(tasks, timeout, failFast);
    }

    public Map<K, T> runAll() {
        if (suppliers.isEmpty()) return Map.of();

        try (var scope = openScope()) {

            Map<K, StructuredTaskScope.Subtask<T>> subtasks = new LinkedHashMap<>();

            for (var entry : suppliers.entrySet()) {
                subtasks.put(entry.getKey(), scope.fork(entry.getValue()::get));
            }

            scope.join();

            Map<K, T> result = new LinkedHashMap<>();

            for (var entry : subtasks.entrySet()) {
                var subtask = entry.getValue();
                if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    result.put(entry.getKey(), subtask.get());
                }
            }

            return Collections.unmodifiableMap(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ExceptionUtils.wrap(e);
        }
    }

    private StructuredTaskScope<T, ?> openScope() {
        StructuredTaskScope.Joiner<T, ?> joiner =
                failFast
                        ? StructuredTaskScope.Joiner.allSuccessfulOrThrow()
                        : StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow();

        return timeout != null
                ? StructuredTaskScope.open(joiner, cfg -> cfg.withTimeout(timeout))
                : StructuredTaskScope.open(joiner);
    }
}
