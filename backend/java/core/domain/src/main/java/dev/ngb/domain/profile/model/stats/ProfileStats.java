package dev.ngb.domain.profile.model.stats;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Denormalized counters for a profile, updated atomically as engagement events occur.
 */
@Getter
public class ProfileStats extends DomainEntity<Long> {

    private ProfileStats() {}

    private Long profileId;
    private Long followerCount;
    private Long followingCount;
    private Long threadCount;
    private Long likeCount;
    private Long mediaCount;

    public static ProfileStats createForNewProfile(Long profileId) {
        ProfileStats obj = new ProfileStats();
        Instant now = Instant.now(obj.clock);
        obj.createdAt = now;
        obj.updatedAt = now;
        obj.profileId = profileId;
        obj.followerCount = 0L;
        obj.followingCount = 0L;
        obj.threadCount = 0L;
        obj.likeCount = 0L;
        obj.mediaCount = 0L;
        return obj;
    }

    public static ProfileStats reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Long followerCount, Long followingCount, Long threadCount,
            Long likeCount, Long mediaCount) {
        ProfileStats obj = new ProfileStats();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.followerCount = followerCount;
        obj.followingCount = followingCount;
        obj.threadCount = threadCount;
        obj.likeCount = likeCount;
        obj.mediaCount = mediaCount;
        return obj;
    }

    public void incrementFollower() {
        this.followerCount = safeIncrement(this.followerCount);
        touch();
    }

    public void decrementFollower() {
        this.followerCount = safeDecrement(this.followerCount);
        touch();
    }

    public void incrementFollowing() {
        this.followingCount = safeIncrement(this.followingCount);
        touch();
    }

    public void decrementFollowing() {
        this.followingCount = safeDecrement(this.followingCount);
        touch();
    }

    private static long safeIncrement(Long current) {
        return current == null ? 1L : current + 1L;
    }

    // Counters are clamped at zero to avoid drift from compensating updates.
    private static long safeDecrement(Long current) {
        if (current == null || current <= 0L) {
            return 0L;
        }
        return current - 1L;
    }

    private void touch() {
        this.updatedAt = Instant.now(clock);
    }
}
