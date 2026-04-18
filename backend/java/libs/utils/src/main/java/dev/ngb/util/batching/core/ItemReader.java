package dev.ngb.util.batching.core;

@FunctionalInterface
public interface ItemReader<I> {
    I read(int shardId, int totalShards) throws Exception;
}
