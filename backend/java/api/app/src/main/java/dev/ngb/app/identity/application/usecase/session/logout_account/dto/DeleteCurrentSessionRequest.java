package dev.ngb.app.identity.application.usecase.session.logout_account.dto;

public record DeleteCurrentSessionRequest(
        String refreshToken
) {
    public DeleteCurrentSessionRequest {
        refreshToken = refreshToken == null ? null : refreshToken.trim();
        if (refreshToken != null && refreshToken.isEmpty()) {
            refreshToken = null;
        }
    }
}
