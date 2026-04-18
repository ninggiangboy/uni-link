package dev.ngb.util.batching;

import dev.ngb.util.batching.core.*;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@UtilityClass
public final class BatchExecutorUtils {

    public static <I, O> void execute(
            int totalShards,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) throws Exception {

        int cores = Runtime.getRuntime().availableProcessors();

        execute(
                totalShards,
                reader,
                processor,
                writer,
                5_000,
                500,
                Math.min(cores, 8),
                10_000
        );
    }

    public static <I, O> void execute(
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) throws Exception {

        execute(
                1,
                reader,
                processor,
                writer
        );
    }

    public static <I, O> void executeCpuBound(
            int totalShards,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) throws Exception {

        int cores = Runtime.getRuntime().availableProcessors();

        execute(
                totalShards,
                reader,
                processor,
                writer,
                2_000,
                200,
                cores,
                4_000
        );
    }

    public static <I, O> void executeCpuBound(
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) throws Exception {


        executeCpuBound(
                1,
                reader,
                processor,
                writer
        );
    }

    public static <I, O> void executeIOBound(
            int totalShards,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) throws Exception {

        int cores = Runtime.getRuntime().availableProcessors();

        execute(
                totalShards,
                reader,
                processor,
                writer,
                10_000,
                100,
                Math.max(cores * 4, 32),
                20_000
        );
    }

    public static <I, O> void executeIOBound(
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer
    ) throws Exception {

        executeIOBound(
                1,
                reader,
                processor,
                writer
        );
    }

    public static <I, O> void execute(
            int totalShards,
            ItemReader<I> reader,
            ItemProcessor<I, O> processor,
            ItemWriter<O> writer,
            int bufferedItemsSize,
            int writeBatchSize,
            int processorConcurrency,
            int maxInflight
    ) throws Exception {

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<AsyncBatchPipeline<I, O>> pipelines = new ArrayList<>();

            for (int i = 0; i < totalShards; i++) {
                AsyncBatchPipeline<I, O> pipeline =
                        AsyncBatchPipeline.<I, O>builder()
                                .shardId(i)
                                .totalShards(totalShards)
                                .reader(reader)
                                .processor(processor)
                                .writer(writer)
                                .readerExecutor(executor)
                                .processorExecutor(executor)
                                .writerExecutor(executor)
                                .bufferedItemsSize(bufferedItemsSize)
                                .writeBatchSize(writeBatchSize)
                                .processorConcurrency(processorConcurrency)
                                .maxInflight(maxInflight)
                                .build();

                pipelines.add(pipeline);
                pipeline.start();
            }

            for (AsyncBatchPipeline<I, O> p : pipelines) {
                p.await();
            }
        }
    }
}
