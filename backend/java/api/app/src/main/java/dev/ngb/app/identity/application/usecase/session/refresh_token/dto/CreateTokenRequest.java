package dev.ngb.app.identity.application.usecase.session.refresh_token.dto;

import dev.ngb.util.validation.FluentValidator;

public record CreateTokenRequest(
        String refreshToken
) {
    public CreateTokenRequest {
        String normalizedRefreshToken = refreshToken == null ? null : refreshToken.trim();
        refreshToken = normalizedRefreshToken;

        FluentValidator.of(this)
                .ruleFor("refreshToken", ignored -> normalizedRefreshToken)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
