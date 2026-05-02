package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.stats.ProfileStats;

import java.util.Optional;

/**
 * Repository for managing {@link ProfileStats} aggregates.
 */
public interface ProfileStatsRepository extends Repository<ProfileStats, Long> {

    Optional<ProfileStats> findByProfileId(Long profileId);

    /**
     * Atomically adjusts {@code follower_count} for {@code profileId}. Negative deltas clamp at zero.
     */
    void adjustFollowerCount(long profileId, long delta);

    /**
     * Atomically adjusts {@code following_count} for {@code profileId}. Negative deltas clamp at zero.
     */
    void adjustFollowingCount(long profileId, long delta);
}

