package dev.ngb.app.identity.application.usecase.authentication.login_account.dto;

public record LoginAccountResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String accountUuid,
        boolean requiresVerification,
        String verificationToken
) {
    public static LoginAccountResponse authenticated(String accessToken, String refreshToken,
                                                     long expiresIn, String accountUuid) {
        return new LoginAccountResponse(accessToken, refreshToken, expiresIn, accountUuid, false, null);
    }

    public static LoginAccountResponse verificationRequired(String verificationToken) {
        return new LoginAccountResponse(null, null, null, null, true, verificationToken);
    }
}
