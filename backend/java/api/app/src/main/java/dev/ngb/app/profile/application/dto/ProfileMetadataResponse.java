package dev.ngb.app.profile.application.dto;

import dev.ngb.domain.profile.model.profile.ProfileMetadata;

public record ProfileMetadataResponse(String key, String value) {
    public static ProfileMetadataResponse of(ProfileMetadata metadata) {
        return new ProfileMetadataResponse(metadata.getKey(), metadata.getValue());
    }
}
