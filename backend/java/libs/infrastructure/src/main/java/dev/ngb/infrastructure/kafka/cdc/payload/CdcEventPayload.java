package dev.ngb.infrastructure.kafka.cdc.payload;

import org.jspecify.annotations.Nullable;

public record CdcEventPayload<T>(EventPayload<T> payload) {

    public record EventPayload<T>(@Nullable T before, T after, String op) {
    }

    public boolean isEvent() {
        return isCreate() && payload != null && payload.after() != null;
    }

    public boolean isCreate() {
        return "c".equalsIgnoreCase(payload.op);
    }

    public boolean isUpdate() {
        return "u".equalsIgnoreCase(payload.op);
    }

    public boolean isDelete() {
        return "d".equalsIgnoreCase(payload.op);
    }
}
