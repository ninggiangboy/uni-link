package dev.ngb.app.profile.application.usecase.link.update_profile_link.dto;

import dev.ngb.domain.profile.model.profile.ProfileLinkType;
import dev.ngb.util.validation.FluentValidator;

public record UpdateProfileLinkRequest(
        ProfileLinkType type,
        String url,
        Integer orderIndex
) {
    public UpdateProfileLinkRequest {
        String normalizedUrl = url == null ? null : url.trim();
        url = normalizedUrl;
        FluentValidator.of(this)
                .ruleFor("url", ignored -> normalizedUrl)
                .nullOr(value -> !value.isBlank() && value.length() <= 500, "must be non-blank and at most 500 chars")
                .ruleFor("orderIndex", UpdateProfileLinkRequest::orderIndex)
                .nullOr(value -> value >= 0, "must be >= 0")
                .validateAndThrow();
    }
}
