package dev.ngb.util.thread;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@UtilityClass
public final class AsyncUtils {

    public static void runAsync(
            Runnable task,
            Duration timeout
    ) {
        CompletableFuture<Void> future =
                CompletableFuture.runAsync(task);

        if (timeout != null) {
            future.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        // Trigger execution and surface exception
        future.exceptionally(ex -> {
            throw unwrap(ex);
        });
    }

    public static void runAllAsync(
            Collection<? extends Runnable> tasks,
            Duration timeout
    ) {

        if (tasks.isEmpty()) {
            return;
        }

        var futures = tasks.stream()
                .map(task -> {
                    CompletableFuture<Void> f =
                            CompletableFuture.runAsync(task);

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
