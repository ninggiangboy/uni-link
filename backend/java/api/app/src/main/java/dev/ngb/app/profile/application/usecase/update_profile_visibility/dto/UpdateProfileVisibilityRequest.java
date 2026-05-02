package dev.ngb.app.profile.application.usecase.update_profile_visibility.dto;

import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.util.validation.FluentValidator;

public record UpdateProfileVisibilityRequest(
        ProfileVisibility visibility
) {
    public UpdateProfileVisibilityRequest {
        // Validate the component parameter — record accessors are unsafe during compact construction.
        FluentValidator.of(visibility)
                .ruleFor("visibility", v -> v)
                .notNull()
                .validateAndThrow();
    }
}
