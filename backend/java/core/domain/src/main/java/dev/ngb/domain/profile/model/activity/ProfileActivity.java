package dev.ngb.domain.profile.model.activity;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Tracks profile activity timestamps for ranking and discovery.
 */
@Getter
public class ProfileActivity extends DomainEntity<Long> {

    private ProfileActivity() {}

    private Long profileId;
    private Instant lastActiveAt;
    private Instant lastPostAt;

    public static ProfileActivity reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Instant lastActiveAt, Instant lastPostAt) {
        ProfileActivity obj = new ProfileActivity();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.lastActiveAt = lastActiveAt;
        obj.lastPostAt = lastPostAt;
        return obj;
    }
}
