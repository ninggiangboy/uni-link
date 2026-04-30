package dev.ngb.app.profile.application.usecase.create_profile.dto;

import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.util.validation.FluentValidator;

public record CreateProfileRequest(
        String username,
        String displayName,
        String bio,
        ProfileVisibility visibility
) {
    public CreateProfileRequest {
        String normalizedUsername = username == null ? null : username.trim();
        String normalizedDisplayName = displayName == null ? null : displayName.trim();
        String normalizedBio = bio == null ? null : bio.trim();
        ProfileVisibility normalizedVisibility = visibility == null ? ProfileVisibility.PUBLIC : visibility;

        username = normalizedUsername;
        displayName = normalizedDisplayName;
        bio = normalizedBio;
        visibility = normalizedVisibility;

        FluentValidator.of(this)
                .ruleFor("username", ignored -> normalizedUsername)
                .notNullOrBlank()
                .minLength(3)
                .maxLength(50)
                .matches("^[A-Za-z0-9_.]+$")
                .ruleFor("displayName", ignored -> normalizedDisplayName)
                .notNullOrBlank()
                .maxLength(100)
                .ruleFor("bio", ignored -> normalizedBio)
                .nullOr(value -> value.length() <= 1000, "length must be at most 1000")
                .ruleFor("visibility", ignored -> normalizedVisibility)
                .notNull()
                .validateAndThrow();
    }
}
