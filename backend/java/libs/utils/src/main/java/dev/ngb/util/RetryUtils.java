package dev.ngb.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public final class RetryUtils {

    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface CheckedConsumer {
        void accept() throws Exception;
    }

    @SneakyThrows
    public static <T> T retry(
            int maxAttempts,
            long baseBackoffMs,
            long maxBackoffMs,
            Class<? extends Exception> retryOn,
            CheckedSupplier<T> supplier
    ) {
        Exception last = null;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return supplier.get();
            } catch (Exception e) {
                last = e;

                if (!retryOn.isInstance(e) || attempt == maxAttempts - 1) {
                    throw e;
                }

                long sleepMs = BackoffUtils.fullJitter(
                        baseBackoffMs,
                        attempt,
                        maxBackoffMs
                );

                TimeUtils.sleep(sleepMs);
            }
        }

        throw Objects.requireNonNull(last);
    }

    @SneakyThrows
    public static void retry(
            int maxAttempts,
            long baseBackoffMs,
            long maxBackoffMs,
            Class<? extends Exception> retryOn,
            CheckedConsumer consumer
    ) {
        retry(
                maxAttempts,
                baseBackoffMs,
                maxBackoffMs,
                retryOn,
                () -> {
                    consumer.accept();
                    return null;
                }
        );
    }
}
