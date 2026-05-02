package dev.ngb.app.profile.application.dto;

import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.util.NullUtils;

public record ProfileSettingResponse(
        boolean allowMentions,
        boolean allowMessages,
        boolean allowTagging,
        boolean showActivityStatus
) {
    public static ProfileSettingResponse of(ProfileSetting setting) {
        return new ProfileSettingResponse(
                NullUtils.getOr(setting.getAllowMentions(), Boolean.TRUE),
                NullUtils.getOr(setting.getAllowMessages(), Boolean.TRUE),
                NullUtils.getOr(setting.getAllowTagging(), Boolean.TRUE),
                NullUtils.getOr(setting.getShowActivityStatus(), Boolean.TRUE)
        );
    }
}
