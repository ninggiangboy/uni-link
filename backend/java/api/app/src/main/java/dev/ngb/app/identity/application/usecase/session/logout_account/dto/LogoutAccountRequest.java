package dev.ngb.app.identity.application.usecase.session.logout_account.dto;

public record LogoutAccountRequest(
        String refreshToken
) {}
