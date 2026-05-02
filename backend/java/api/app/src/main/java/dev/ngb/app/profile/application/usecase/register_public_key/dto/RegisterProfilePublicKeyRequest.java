package dev.ngb.app.profile.application.usecase.register_public_key.dto;

import dev.ngb.util.validation.FluentValidator;

public record RegisterProfilePublicKeyRequest(String publicKey) {
    public RegisterProfilePublicKeyRequest {
        String normalized = publicKey == null ? null : publicKey.trim();
        publicKey = normalized;
        FluentValidator.of(this)
                .ruleFor("publicKey", ignored -> normalized)
                .notNullOrBlank()
                .maxLength(2048)
                .validateAndThrow();
    }
}
