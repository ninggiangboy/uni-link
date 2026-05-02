package dev.ngb.domain.profile.model.stats;

/**
 * Aggregated counter changes for one profile row in {@code prf_profile_stats} (e.g. one Kafka batch).
 */
public record ProfileStatsCountDelta(long profileId, long followerDelta, long followingDelta) {
}
