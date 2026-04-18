package dev.ngb.app.identity.application.usecase.password.reset_password.dto;

import dev.ngb.util.validation.FluentValidator;

public record ResetPasswordRequest(
        String email,
        String otpCode,
        String newPassword
) {
    public ResetPasswordRequest {
        String normalizedEmail = email == null ? null : email.trim();
        String normalizedOtpCode = otpCode == null ? null : otpCode.trim();
        String normalizedNewPassword = newPassword == null ? null : newPassword.trim();
        email = normalizedEmail;
        otpCode = normalizedOtpCode;
        newPassword = normalizedNewPassword;

        FluentValidator.of(this)
                .ruleFor("email", ignored -> normalizedEmail)
                .notNullOrBlank()
                .email()
                .ruleFor("otpCode", ignored -> normalizedOtpCode)
                .notNullOrBlank()
                .ruleFor("newPassword", ignored -> normalizedNewPassword)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
