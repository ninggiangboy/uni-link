package dev.ngb.app.profile.application.usecase.update_profile.dto;

import dev.ngb.util.validation.FluentValidator;

public record UpdateProfileRequest(
        String displayName,
        String bio,
        String website,
        String location
) {
    public UpdateProfileRequest {
        String normalizedDisplayName = displayName == null ? null : displayName.trim();
        String normalizedBio = bio == null ? null : bio.trim();
        String normalizedWebsite = website == null ? null : website.trim();
        String normalizedLocation = location == null ? null : location.trim();

        displayName = normalizedDisplayName;
        bio = normalizedBio;
        website = normalizedWebsite;
        location = normalizedLocation;

        FluentValidator.of(this)
                .ruleFor("displayName", ignored -> normalizedDisplayName)
                .notNullOrBlank()
                .maxLength(100)
                .ruleFor("bio", ignored -> normalizedBio)
                .nullOr(value -> value.length() <= 1000, "length must be at most 1000")
                .ruleFor("website", ignored -> normalizedWebsite)
                .nullOr(value -> value.length() <= 500, "length must be at most 500")
                .ruleFor("location", ignored -> normalizedLocation)
                .nullOr(value -> value.length() <= 255, "length must be at most 255")
                .validateAndThrow();
    }
}
