package dev.ngb.app.identity.application.usecase.session.logout_account.dto;

public record LogoutAccountRequest(
        String refreshToken
) {
    public LogoutAccountRequest {
        refreshToken = refreshToken == null ? null : refreshToken.trim();
        if (refreshToken != null && refreshToken.isEmpty()) {
            refreshToken = null;
        }
    }
}
