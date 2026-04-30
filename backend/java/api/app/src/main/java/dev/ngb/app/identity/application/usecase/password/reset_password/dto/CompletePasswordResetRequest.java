package dev.ngb.app.identity.application.usecase.password.reset_password.dto;

import dev.ngb.util.validation.FluentValidator;

public record CompletePasswordResetRequest(
        String otpCode,
        String newPassword
) {
    public CompletePasswordResetRequest {
        String normalizedOtpCode = otpCode == null ? null : otpCode.trim();
        String normalizedNewPassword = newPassword == null ? null : newPassword.trim();
        otpCode = normalizedOtpCode;
        newPassword = normalizedNewPassword;

        FluentValidator.of(this)
                .ruleFor("otpCode", ignored -> normalizedOtpCode)
                .notNullOrBlank()
                .ruleFor("newPassword", ignored -> normalizedNewPassword)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
