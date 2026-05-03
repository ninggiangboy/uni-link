package dev.ngb.domain.profile.model.username;

import dev.ngb.domain.DomainEntity;
import dev.ngb.util.StringUtils;
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

    /**
     * Creates a new history row marked as the active username.
     */
    public static ProfileUsername createCurrent(Long profileId, String username) {
        ProfileUsername obj = new ProfileUsername();

        obj.profileId = profileId;
        obj.username = StringUtils.trim(username);
        obj.isCurrent = Boolean.TRUE;
        return obj;
    }

    /**
     * Demotes this row to a historical (non-current) entry.
     */
    public void markHistorical() {
        this.isCurrent = Boolean.FALSE;
    }
}
