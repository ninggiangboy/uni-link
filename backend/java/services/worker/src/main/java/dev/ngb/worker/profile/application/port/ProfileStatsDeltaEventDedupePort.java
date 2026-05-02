package dev.ngb.worker.profile.application.port;

/**
 * Ensures each {@link dev.ngb.event.ProfileFollowStatsDeltaEvent} is applied at most once.
 */
public interface ProfileStatsDeltaEventDedupePort {

    /**
     * @return {@code true} if this uuid was newly recorded and the caller should apply deltas;
     * {@code false} if it was already processed (duplicate delivery).
     */
    boolean tryClaimEvent(String eventUuid);
}
