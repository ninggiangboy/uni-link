package dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto;

public record CreateOAuthSessionResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String accountUuid,
        boolean isNewAccount
) {}
