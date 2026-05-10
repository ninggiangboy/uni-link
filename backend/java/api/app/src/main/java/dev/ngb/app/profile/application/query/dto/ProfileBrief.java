package dev.ngb.app.profile.application.query.dto;

import dev.ngb.domain.profile.model.profile.Profile;

/**
 * Lightweight profile representation used for paginated relationship lists
 * (followers, following, blocked, muted) where we don't want to ship the full
 * {@link ProfileSummary} for each row.
 */
public record ProfileBrief(
        String profileUuid,
        String username,
        String displayName,
        String avatarUrl,
        Boolean isVerified
) {
    public static ProfileBrief of(Profile profile) {
        return new ProfileBrief(
                profile.getUuid(),
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getIsVerified()
        );
    }
}
