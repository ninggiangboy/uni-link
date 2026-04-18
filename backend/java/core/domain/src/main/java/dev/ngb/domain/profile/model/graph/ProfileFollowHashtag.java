package dev.ngb.domain.profile.model.graph;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Allows a profile to follow a hashtag and receive its feed.
 */
@Getter
public class ProfileFollowHashtag extends DomainEntity<Long> {

    private ProfileFollowHashtag() {}

    private Long profileId;
    private Long hashtagId;

    public static ProfileFollowHashtag reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Long hashtagId) {
        ProfileFollowHashtag obj = new ProfileFollowHashtag();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.hashtagId = hashtagId;
        return obj;
    }
}
