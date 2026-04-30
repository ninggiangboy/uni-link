package dev.ngb.app.identity.application.usecase.registration.verify_email.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.util.validation.FluentValidator;

public record CompleteEmailVerificationRequest(
        String otpCode,
        DeviceInfo deviceInfo
) {
    public CompleteEmailVerificationRequest {
        String normalizedOtpCode = otpCode == null ? null : otpCode.trim();
        otpCode = normalizedOtpCode;

        FluentValidator.of(this)
                .ruleFor("otpCode", ignored -> normalizedOtpCode)
                .notNullOrBlank()
                .ruleFor("deviceInfo", ignored -> deviceInfo)
                .notNull()
                .validateAndThrow();
    }
}
