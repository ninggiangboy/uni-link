package dev.ngb.app.profile.application.usecase.update_profile_setting.dto;

/**
 * Partial update DTO. Each {@code null} field leaves the existing flag unchanged.
 */
public record UpdateProfileSettingRequest(
        Boolean allowMentions,
        Boolean allowMessages,
        Boolean allowTagging,
        Boolean showActivityStatus
) {}
