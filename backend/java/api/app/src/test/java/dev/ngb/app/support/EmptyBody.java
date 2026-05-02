package dev.ngb.app.support;

/**
 * Success marker for HTTP responses with no JSON body (e.g. 204). Prefer this over {@link Void}
 * because {@link io.vavr.control.Either} cannot hold {@code null} on the right side.
 */
public enum EmptyBody {
    INSTANCE
}
