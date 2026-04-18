package dev.ngb.domain.profile.model.graph;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Muting hides a profile's content without blocking.
 * Unlike block, the muted profile is not aware of being muted.
 */
@Getter
public class ProfileMute extends DomainEntity<Long> {

    private ProfileMute() {}

    private Long muterProfileId;
    private Long mutedProfileId;

    public static ProfileMute reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long muterProfileId, Long mutedProfileId) {
        ProfileMute obj = new ProfileMute();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.muterProfileId = muterProfileId;
        obj.mutedProfileId = mutedProfileId;
        return obj;
    }
}
