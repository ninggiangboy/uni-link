package dev.ngb.app.profile.application.usecase.upsert_profile_metadata.dto;

import dev.ngb.app.profile.ProfileConstants;
import dev.ngb.util.validation.FluentValidator;

public record UpsertProfileMetadataRequest(String value) {
    public UpsertProfileMetadataRequest {
        FluentValidator.of(this)
                .ruleFor("value", UpsertProfileMetadataRequest::value)
                .nullOr(v -> v.length() <= ProfileConstants.MAX_METADATA_VALUE_LENGTH,
                        "length must be at most " + ProfileConstants.MAX_METADATA_VALUE_LENGTH)
                .validateAndThrow();
    }
}
