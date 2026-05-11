package dev.ngb.app.profile.application.dto;

import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.util.NullUtils;

import java.time.Instant;

/**
 * Read model for a profile. {@code stats} fields default to zero when no
 * {@link ProfileStats} row exists yet (e.g. immediately after profile creation).
 * For viewers without permission to see private fields, the use case strips
 * {@code bio}/{@code website}/{@code location} before constructing this DTO.
 */
public record ProfileSummary(
        String profileUuid,
        String username,
        String displayName,
        String bio,
        String website,
        String location,
        String avatarUrl,
        String bannerUrl,
        ProfileVisibility visibility,
        Boolean isVerified,
        long followerCount,
        long followingCount,
        long threadCount,
        long likeCount,
        Instant createdAt
) {
    public static ProfileSummary of(Profile profile, ProfileStats stats) {
        return of(profile, stats, 0L, 0L);
    }

    public static ProfileSummary of(Profile profile, ProfileStats stats, long followerDelta, long followingDelta) {
        long baseFollower = stats == null ? 0L : NullUtils.getOrZero(stats.getFollowerCount());
        long baseFollowing = stats == null ? 0L : NullUtils.getOrZero(stats.getFollowingCount());
        return new ProfileSummary(
                profile.getUuid(),
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getWebsite(),
                profile.getLocation(),
                profile.getAvatarUrl(),
                profile.getBannerUrl(),
                profile.getVisibility(),
                NullUtils.getOr(profile.getIsVerified(), Boolean.FALSE),
                Math.max(0, baseFollower + followerDelta),
                Math.max(0, baseFollowing + followingDelta),
                stats == null ? 0L : NullUtils.getOrZero(stats.getThreadCount()),
                stats == null ? 0L : NullUtils.getOrZero(stats.getLikeCount()),
                profile.getCreatedAt()
        );
    }

    /**
     * Strips fields not visible to the requesting viewer (bio/website/location/stats).
     */
    public ProfileSummary withRestrictedFields() {
        return new ProfileSummary(
                profileUuid, username, displayName,
                null, null, null,
                avatarUrl, bannerUrl,
                visibility, isVerified,
                followerCount, followingCount, 0L, 0L,
                createdAt
        );
    }
}
