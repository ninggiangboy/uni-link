package dev.ngb.app.profile.application.usecase.add_profile_link.dto;

import dev.ngb.domain.profile.model.profile.ProfileLinkType;
import dev.ngb.util.validation.FluentValidator;

public record AddProfileLinkRequest(
        ProfileLinkType type,
        String url,
        Integer orderIndex
) {
    public AddProfileLinkRequest {
        String normalizedUrl = url == null ? null : url.trim();
        ProfileLinkType normalizedType = type == null ? ProfileLinkType.OTHER : type;
        url = normalizedUrl;
        type = normalizedType;

        FluentValidator.of(this)
                .ruleFor("url", ignored -> normalizedUrl)
                .notNullOrBlank()
                .maxLength(500)
                .ruleFor("orderIndex", AddProfileLinkRequest::orderIndex)
                .nullOr(value -> value >= 0, "must be >= 0")
                .validateAndThrow();
    }
}
