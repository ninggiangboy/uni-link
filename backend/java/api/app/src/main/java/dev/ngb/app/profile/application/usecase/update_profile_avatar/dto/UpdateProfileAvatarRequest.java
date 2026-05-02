package dev.ngb.app.profile.application.usecase.update_profile_avatar.dto;

import dev.ngb.util.validation.FluentValidator;

public record UpdateProfileAvatarRequest(String attachmentUuid) {
    public UpdateProfileAvatarRequest {
        String normalized = attachmentUuid == null ? null : attachmentUuid.trim();
        attachmentUuid = normalized;
        FluentValidator.of(this)
                .ruleFor("attachmentUuid", ignored -> normalized)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
