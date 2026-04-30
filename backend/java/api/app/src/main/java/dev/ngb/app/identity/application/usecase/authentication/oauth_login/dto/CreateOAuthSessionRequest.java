package dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.util.validation.FluentValidator;

public record CreateOAuthSessionRequest(
        AuthProvider provider,
        String providerToken,
        DeviceInfo deviceInfo
) {
    public CreateOAuthSessionRequest {
        String normalizedProviderToken = providerToken == null ? null : providerToken.trim();
        providerToken = normalizedProviderToken;

        FluentValidator.of(this)
                .ruleFor("provider", ignored -> provider)
                .notNull()
                .ruleFor("providerToken", ignored -> normalizedProviderToken)
                .notNullOrBlank()
                .ruleFor("deviceInfo", ignored -> deviceInfo)
                .notNull()
                .validateAndThrow();
    }
}
