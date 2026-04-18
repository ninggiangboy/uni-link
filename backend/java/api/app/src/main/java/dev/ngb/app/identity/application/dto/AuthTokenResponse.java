package dev.ngb.app.identity.application.dto;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String accountUuid
) {}
