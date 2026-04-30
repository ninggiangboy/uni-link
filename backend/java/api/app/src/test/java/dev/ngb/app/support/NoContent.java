package dev.ngb.app.support;

/**
 * Success payload for HTTP responses with no JSON body (e.g. 204).
 */
public final class NoContent {

    public static final NoContent INSTANCE = new NoContent();

    private NoContent() {
    }
}
