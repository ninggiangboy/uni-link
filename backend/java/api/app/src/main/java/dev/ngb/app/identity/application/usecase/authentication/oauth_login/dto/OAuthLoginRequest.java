package dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OAuthLoginRequest(
        @NotNull
        AuthProvider provider,
        @NotBlank
        String providerToken,
        @NotNull @Valid
        DeviceInfo deviceInfo
) {}
