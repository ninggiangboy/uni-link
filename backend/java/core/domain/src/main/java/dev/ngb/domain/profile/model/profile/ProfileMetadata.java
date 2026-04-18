package dev.ngb.domain.profile.model.profile;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Extensible key-value metadata for a profile.
 * e.g. creator_category, pronouns, theme_color.
 */
@Getter
public class ProfileMetadata extends DomainEntity<Long> {

    private ProfileMetadata() {}

    private Long profileId;
    private String key;
    private String value;

    public static ProfileMetadata reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, String key, String value) {
        ProfileMetadata obj = new ProfileMetadata();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.key = key;
        obj.value = value;
        return obj;
    }
}
