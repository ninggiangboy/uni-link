package dev.ngb.domain.profile.model.setting;

import dev.ngb.domain.DomainEntity;
import dev.ngb.util.NullUtils;
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

    /**
     * Creates a fresh setting row with all interaction toggles enabled.
     */
    public static ProfileSetting createDefault(Long profileId) {
        ProfileSetting obj = new ProfileSetting();

        obj.profileId = profileId;
        obj.allowMentions = Boolean.TRUE;
        obj.allowMessages = Boolean.TRUE;
        obj.allowTagging = Boolean.TRUE;
        obj.showActivityStatus = Boolean.TRUE;
        return obj;
    }

    /**
     * Partial update: each {@code null} parameter leaves the corresponding flag unchanged.
     */
    public void update(Boolean allowMentions, Boolean allowMessages, Boolean allowTagging, Boolean showActivityStatus) {
        this.allowMentions = NullUtils.getOr(allowMentions, this.allowMentions);
        this.allowMessages = NullUtils.getOr(allowMessages, this.allowMessages);
        this.allowTagging = NullUtils.getOr(allowTagging, this.allowTagging);
        this.showActivityStatus = NullUtils.getOr(showActivityStatus, this.showActivityStatus);
    }
}
