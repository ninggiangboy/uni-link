package dev.ngb.app.identity.application.usecase.registration.verify_email.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.util.validation.FluentValidator;

public record VerifyEmailRequest(
        String email,
        String otpCode,
        DeviceInfo deviceInfo
) {
    public VerifyEmailRequest {
        String normalizedEmail = email == null ? null : email.trim();
        String normalizedOtpCode = otpCode == null ? null : otpCode.trim();
        email = normalizedEmail;
        otpCode = normalizedOtpCode;

        FluentValidator.of(this)
                .ruleFor("email", ignored -> normalizedEmail)
                .notNullOrBlank()
                .email()
                .ruleFor("otpCode", ignored -> normalizedOtpCode)
                .notNullOrBlank()
                .ruleFor("deviceInfo", ignored -> deviceInfo)
                .notNull()
                .validateAndThrow();
    }
}
