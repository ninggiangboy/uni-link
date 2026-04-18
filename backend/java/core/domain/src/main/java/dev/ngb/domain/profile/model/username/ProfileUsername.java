package dev.ngb.domain.profile.model.username;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Tracks username history for a profile.
 * Enables redirecting old usernames and auditing changes.
 */
@Getter
public class ProfileUsername extends DomainEntity<Long> {

    private ProfileUsername() {}

    private Long profileId;
    private String username;
    private Boolean isCurrent;

    public static ProfileUsername reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, String username, Boolean isCurrent) {
        ProfileUsername obj = new ProfileUsername();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.username = username;
        obj.isCurrent = isCurrent;
        return obj;
    }
}
