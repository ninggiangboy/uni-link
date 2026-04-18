package dev.ngb.domain.profile.model.setting;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Per-profile privacy and interaction settings.
 * Separated from profile to keep the core entity lean.
 */
@Getter
public class ProfileSetting extends DomainEntity<Long> {

    private ProfileSetting() {}

    private Long profileId;
    private Boolean allowMentions;
    private Boolean allowMessages;
    private Boolean allowTagging;
    private Boolean showActivityStatus;

    public static ProfileSetting reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Boolean allowMentions, Boolean allowMessages, Boolean allowTagging,
            Boolean showActivityStatus) {
        ProfileSetting obj = new ProfileSetting();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.allowMentions = allowMentions;
        obj.allowMessages = allowMessages;
        obj.allowTagging = allowTagging;
        obj.showActivityStatus = showActivityStatus;
        return obj;
    }
}
