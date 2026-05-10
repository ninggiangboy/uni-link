package dev.ngb.util.thread;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class AsyncUtils {

    private static final Executor DEFAULT_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static void runAsync(
            Runnable task,
            Duration timeout
    ) {
        runAsync(task, timeout, DEFAULT_EXECUTOR);
    }

    public static void runAsync(
            Runnable task,
            Duration timeout,
            Executor executor
    ) {
        CompletableFuture<Void> future =
                CompletableFuture.runAsync(task, executor);

        if (timeout != null) {
            future.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        future.exceptionally(ex -> {
            throw unwrap(ex);
        });
    }

    public static void runAllAsync(
            Collection<? extends Runnable> tasks,
            Duration timeout
    ) {
        runAllAsync(tasks, timeout, DEFAULT_EXECUTOR);
    }

    public static void runAllAsync(
            Collection<? extends Runnable> tasks,
            Duration timeout,
            Executor executor
    ) {
        if (tasks.isEmpty()) {
            return;
        }

        var futures = tasks.stream()
                .map(task -> {
                    CompletableFuture<Void> f =
                            CompletableFuture.runAsync(task, executor);

                    if (timeout != null) {
                        f = f.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
                    }

                    return f;
                })
                .toList();

        futures.forEach(f ->
                f.exceptionally(ex -> {
                    throw unwrap(ex);
                })
        );
    }

    private static RuntimeException unwrap(Throwable ex) {
        if (ex instanceof CompletionException ce && ce.getCause() != null) {
            ex = ce.getCause();
        }
        return ex instanceof RuntimeException re
                ? re
                : new RuntimeException(ex);
    }
}
