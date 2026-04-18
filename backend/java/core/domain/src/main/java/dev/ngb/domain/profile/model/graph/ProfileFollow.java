package dev.ngb.domain.profile.model.graph;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a follow relationship between two profiles.
 */
@Getter
public class ProfileFollow extends DomainEntity<Long> {

    private ProfileFollow() {}

    private Long followerProfileId;
    private Long followingProfileId;

    public static ProfileFollow reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long followerProfileId, Long followingProfileId) {
        ProfileFollow obj = new ProfileFollow();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.followerProfileId = followerProfileId;
        obj.followingProfileId = followingProfileId;
        return obj;
    }
}
