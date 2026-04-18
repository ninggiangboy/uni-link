package dev.ngb.util.batching.core;

import java.util.List;

@FunctionalInterface
public interface ItemWriter<O> {
    void write(List<O> items) throws Exception;
}
