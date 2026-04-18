package dev.ngb.domain.profile.model.profile;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * External link displayed on a profile (e.g. website, social media).
 */
@Getter
public class ProfileLink extends DomainEntity<Long> {

    private ProfileLink() {}

    private Long profileId;
    private ProfileLinkType type;
    private String url;
    private Integer orderIndex;

    public static ProfileLink reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, ProfileLinkType type, String url, Integer orderIndex) {
        ProfileLink obj = new ProfileLink();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.type = type;
        obj.url = url;
        obj.orderIndex = orderIndex;
        return obj;
    }
}
