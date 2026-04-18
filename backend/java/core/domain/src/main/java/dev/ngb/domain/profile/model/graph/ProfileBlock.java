package dev.ngb.domain.profile.model.graph;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile blocking another profile, preventing all interaction.
 */
@Getter
public class ProfileBlock extends DomainEntity<Long> {

    private ProfileBlock() {}

    private Long blockerProfileId;
    private Long blockedProfileId;

    public static ProfileBlock reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long blockerProfileId, Long blockedProfileId) {
        ProfileBlock obj = new ProfileBlock();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.blockerProfileId = blockerProfileId;
        obj.blockedProfileId = blockedProfileId;
        return obj;
    }
}
