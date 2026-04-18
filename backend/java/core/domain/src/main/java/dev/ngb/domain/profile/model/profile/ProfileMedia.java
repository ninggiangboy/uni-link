package dev.ngb.domain.profile.model.profile;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Media asset associated with a profile (avatar, banner, or highlight).
 */
@Getter
public class ProfileMedia extends DomainEntity<Long> {

    private ProfileMedia() {}

    private Long profileId;
    private ProfileMediaType type;
    private String url;
    private String metadata;

    public static ProfileMedia reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, ProfileMediaType type, String url, String metadata) {
        ProfileMedia obj = new ProfileMedia();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.type = type;
        obj.url = url;
        obj.metadata = metadata;
        return obj;
    }
}
