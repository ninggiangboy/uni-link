package dev.ngb.app.profile.support;

import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.model.profile.ProfileLinkType;
import dev.ngb.domain.profile.model.profile.ProfileMetadata;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.model.relationship.FollowRequestStatus;
import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.model.username.ProfileUsername;
import java.time.Instant;

/**
 * Helpers for constructing fully-hydrated domain objects in unit tests.
 */
public final class ProfileFixtures {

    private ProfileFixtures() {}

    public static Profile profile(Long id, Long accountId, String username) {
        return profile(id, accountId, username, ProfileVisibility.PUBLIC);
    }

    public static Profile profile(Long id, Long accountId, String username, ProfileVisibility visibility) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return Profile.reconstruct(
                id,
                "profile-" + id,
                null,
                now,
                null,
                now,
                accountId,
                username,
                username + " display",
                "bio for " + username,
                null,
                null,
                null,
                null,
                visibility,
                Boolean.FALSE,
                null
        );
    }

    public static ProfileSetting defaultSetting(Long id, Long profileId) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return ProfileSetting.reconstruct(
                id, "setting-" + id, null, now, null, now,
                profileId, true, true, true, true
        );
    }

    public static ProfileStats stats(Long id, Long profileId, long followers, long following) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return ProfileStats.reconstruct(
                id, "stats-" + id, null, now, null, now,
                profileId, followers, following, 0L, 0L, 0L
        );
    }

    public static ProfileLink link(Long id, Long profileId, String url) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return ProfileLink.reconstruct(
                id, "link-" + id, null, now, null, now,
                profileId, ProfileLinkType.OTHER, url, 0
        );
    }

    public static ProfileMetadata metadata(Long id, Long profileId, String key, String value) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return ProfileMetadata.reconstruct(
                id, "meta-" + id, null, now, null, now,
                profileId, key, value
        );
    }

    public static ProfileUsername currentUsername(Long id, Long profileId, String username) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return ProfileUsername.reconstruct(
                id, "uname-" + id, null, now, null, now,
                profileId, username, true
        );
    }

    public static FollowRequest pendingRequest(Long id, Long requesterId, Long targetId) {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        return FollowRequest.reconstruct(
                id, "req-" + id, null, now, null, now,
                requesterId, targetId, FollowRequestStatus.PENDING, null
        );
    }
}
