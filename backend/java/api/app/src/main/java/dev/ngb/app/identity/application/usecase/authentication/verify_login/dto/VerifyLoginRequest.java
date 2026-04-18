package dev.ngb.app.identity.application.usecase.authentication.verify_login.dto;

import dev.ngb.util.validation.FluentValidator;

public record VerifyLoginRequest(
        String verificationToken,
        String otpCode
) {
    public VerifyLoginRequest {
        String normalizedVerificationToken = verificationToken == null ? null : verificationToken.trim();
        String normalizedOtpCode = otpCode == null ? null : otpCode.trim();
        verificationToken = normalizedVerificationToken;
        otpCode = normalizedOtpCode;

        FluentValidator.of(this)
                .ruleFor("verificationToken", ignored -> normalizedVerificationToken)
                .notNullOrBlank()
                .ruleFor("otpCode", ignored -> normalizedOtpCode)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
