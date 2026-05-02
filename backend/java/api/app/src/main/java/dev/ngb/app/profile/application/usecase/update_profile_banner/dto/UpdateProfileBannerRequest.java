package dev.ngb.app.profile.application.usecase.update_profile_banner.dto;

import dev.ngb.util.validation.FluentValidator;

public record UpdateProfileBannerRequest(String attachmentUuid) {
    public UpdateProfileBannerRequest {
        String normalized = attachmentUuid == null ? null : attachmentUuid.trim();
        attachmentUuid = normalized;
        FluentValidator.of(this)
                .ruleFor("attachmentUuid", ignored -> normalized)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
