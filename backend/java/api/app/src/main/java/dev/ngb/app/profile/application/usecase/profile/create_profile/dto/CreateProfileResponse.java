package dev.ngb.app.profile.application.usecase.profile.create_profile.dto;

import dev.ngb.domain.profile.model.profile.ProfileVisibility;

import java.time.Instant;

public record CreateProfileResponse(
        String profileUuid,
        String username,
        String displayName,
        ProfileVisibility visibility,
        Instant createdAt
) {}
