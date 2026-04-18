package dev.ngb.util.batching;

import dev.ngb.util.batching.core.ItemProcessor;
import dev.ngb.util.batching.core.ItemReader;
import dev.ngb.util.batching.core.ItemWriter;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncBatchPipeline<I, O> {

    sealed interface Item<T> permits Data, End {
    }

    record Data<T>(T value) implements Item<T> {
    }

    record End<T>() implements Item<T> {
    }

    @SuppressWarnings("rawtypes")
    private static final End END = new End<>();

    @SuppressWarnings("unchecked")
    private static <T> End<T> end() {
        return (End<T>) END;
    }

    private final int shardId;
    private final int totalShards;
    private final int writeBatchSize;
    private final int processorConcurrency;

    private final ItemReader<I> reader;
    private final ItemProcessor<I, O> processor;
    private final ItemWriter<O> writer;

    private final ExecutorService readerExecutor;
    private final ExecutorService processorExecutor;
    private final ExecutorService writerExecutor;

    private final BlockingQueue<Item<I>> inputQueue;
    private final BlockingQueue<Item<O>> outputQueue;
    private final Semaphore inflight;

    private final List<Future<?>> futures = new ArrayList<>();
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    @Getter
    private final AtomicLong itemsRead = new AtomicLong();
    @Getter
    private final AtomicLong itemsProcessed = new AtomicLong();
    @Getter
    private final AtomicLong itemsWritten = new AtomicLong();

    @Builder
    public AsyncBatchPipeline(
            int shardId,
            int totalShards,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer,
            ExecutorService readerExecutor,
            ExecutorService processorExecutor,
            ExecutorService writerExecutor,
            int bufferedItemsSize,
            int writeBatchSize,
            int processorConcurrency,
            int maxInflight
    ) {
        if (totalShards <= 0) {
            throw new IllegalArgumentException("totalShards must be > 0");
        }

        if (bufferedItemsSize <= 0) {
            throw new IllegalArgumentException("bufferedItemsSize must be > 0");
        }

        if (writeBatchSize <= 0) {
            throw new IllegalArgumentException("writeBatchSize must be > 0");
        }

        if (processorConcurrency <= 0) {
            throw new IllegalArgumentException("processorConcurrency must be > 0");
        }

        if (maxInflight < writeBatchSize) {
            throw new IllegalArgumentException(
                    "maxInflight must be >= writeBatchSize"
            );
        }

        this.shardId = shardId;
        this.totalShards = totalShards;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.writeBatchSize = writeBatchSize;
        this.processorConcurrency = processorConcurrency;

        this.readerExecutor = readerExecutor;
        this.processorExecutor = processorExecutor;
        this.writerExecutor = writerExecutor;

        this.inputQueue = new ArrayBlockingQueue<>(bufferedItemsSize);
        this.outputQueue = new ArrayBlockingQueue<>(bufferedItemsSize);
        this.inflight = new Semaphore(maxInflight);
    }

    public void start() {
        futures.add(readerExecutor.submit(this::safeRunReader));

        for (int i = 0; i < processorConcurrency; i++) {
            futures.add(processorExecutor.submit(this::safeRunProcessor));
        }

        futures.add(writerExecutor.submit(this::safeRunWriter));
    }

    public void await() throws Exception {
        try {
            for (Future<?> f : futures) {
                f.get();
            }
        } catch (ExecutionException e) {
            cancelAll();
            throw unwrap(e);
        } catch (InterruptedException e) {
            cancelAll();
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    private void cancelAll() throws InterruptedException {
        if (stopped.compareAndSet(false, true)) {
            futures.forEach(f -> f.cancel(true));
            inflight.drainPermits();
            inflight.release(Integer.MAX_VALUE / 2);
            inputQueue.clear();
            inputQueue.put(end());
            outputQueue.clear();
            outputQueue.put(end());
        }
    }

    private Exception unwrap(ExecutionException e) {
        Throwable cause = e.getCause();
        return cause instanceof Exception ex ? ex : new RuntimeException("Pipeline failed", cause);
    }

    private Void safeRunReader() throws Exception {
        try {
            return runReader();
        } catch (Throwable t) {
            cancelAll();
            throw t;
        }
    }

    private Void runReader() throws Exception {
        while (!stopped.get() && !Thread.currentThread().isInterrupted()) {
            I data = reader.read(shardId, totalShards);
            if (data == null) break;

            inflight.acquire();
            inputQueue.put(new Data<>(data));
            itemsRead.incrementAndGet();
        }

        for (int i = 0; i < processorConcurrency; i++) {
            inputQueue.put(end());
        }

        return null;
    }

    private Void safeRunProcessor() throws Exception {
        try {
            return runProcessor();
        } catch (Throwable t) {
            cancelAll();
            throw t;
        }
    }

    private Void runProcessor() throws Exception {
        while (!stopped.get() && !Thread.currentThread().isInterrupted()) {
            Item<I> item = inputQueue.take();

            if (item instanceof End) break;

            if (item instanceof Data<I>(I value)) {
                O out = processor.process(value);

                if (out != null) {
                    outputQueue.put(new Data<>(out));
                    itemsProcessed.incrementAndGet();
                } else {
                    inflight.release();
                }
            }
        }

        outputQueue.put(end());
        return null;
    }

    private Void safeRunWriter() throws Exception {
        try {
            return runWriter();
        } catch (Throwable t) {
            cancelAll();
            throw t;
        }
    }

    private Void runWriter() throws Exception {
        AtomicInteger endSignals = new AtomicInteger(0);
        List<O> buffer = new ArrayList<>(writeBatchSize);

        while (!stopped.get() && !Thread.currentThread().isInterrupted()) {
            Item<O> item = outputQueue.take();

            if (item instanceof End) {
                if (endSignals.incrementAndGet() >= processorConcurrency) break;
                continue;
            }

            if (item instanceof Data<O>(O value)) {
                buffer.add(value);

                if (buffer.size() >= writeBatchSize) {
                    flush(buffer);
                }
            }
        }

        if (!buffer.isEmpty()) {
            flush(buffer);
        }

        return null;
    }

    private void flush(List<O> buffer) throws Exception {
        List<O> batch = new ArrayList<>(buffer);
        writer.write(batch);
        itemsWritten.addAndGet(batch.size());
        inflight.release(batch.size());
        buffer.clear();
    }
}
