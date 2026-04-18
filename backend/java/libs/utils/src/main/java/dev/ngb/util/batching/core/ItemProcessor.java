package dev.ngb.util.batching.core;

@FunctionalInterface
public interface ItemProcessor<I, O> {
    O process(I item) throws Exception;
}
