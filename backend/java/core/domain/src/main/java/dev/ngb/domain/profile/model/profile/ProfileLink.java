package dev.ngb.domain.profile.model.profile;

import dev.ngb.domain.DomainEntity;
import dev.ngb.util.NullUtils;
import dev.ngb.util.StringUtils;
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

    public static ProfileLink create(Long profileId, ProfileLinkType type, String url, Integer orderIndex) {
        ProfileLink obj = new ProfileLink();

        obj.profileId = profileId;
        obj.type = NullUtils.getOr(type, ProfileLinkType.OTHER);
        obj.url = StringUtils.trim(url);
        obj.orderIndex = NullUtils.getOrZero(orderIndex);
        return obj;
    }

    /**
     * Partial update: {@code null} parameters leave the corresponding field untouched.
     */
    public void update(ProfileLinkType type, String url, Integer orderIndex) {
        if (type != null) this.type = type;
        if (url != null) this.url = StringUtils.trim(url);
        if (orderIndex != null) this.orderIndex = orderIndex;
    }
}
