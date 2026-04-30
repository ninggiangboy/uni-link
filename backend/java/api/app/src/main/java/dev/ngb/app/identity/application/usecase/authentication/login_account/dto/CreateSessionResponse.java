package dev.ngb.app.identity.application.usecase.authentication.login_account.dto;

public record CreateSessionResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String accountUuid,
        boolean requiresVerification,
        String verificationToken
) {
    public static CreateSessionResponse authenticated(String accessToken, String refreshToken,
                                                     long expiresIn, String accountUuid) {
        return new CreateSessionResponse(accessToken, refreshToken, expiresIn, accountUuid, false, null);
    }

    public static CreateSessionResponse verificationRequired(String verificationToken) {
        return new CreateSessionResponse(null, null, null, null, true, verificationToken);
    }
}
