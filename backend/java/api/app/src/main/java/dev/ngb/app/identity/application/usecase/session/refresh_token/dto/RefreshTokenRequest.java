package dev.ngb.app.identity.application.usecase.session.refresh_token.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank
        String refreshToken
) {}
