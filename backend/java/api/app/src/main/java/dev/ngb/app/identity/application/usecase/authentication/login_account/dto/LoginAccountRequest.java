package dev.ngb.app.identity.application.usecase.authentication.login_account.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.util.validation.FluentValidator;

public record LoginAccountRequest(
        String email,
        String password,
        DeviceInfo deviceInfo
) {
    public LoginAccountRequest {
        String normalizedEmail = email == null ? null : email.trim();
        String normalizedPassword = password == null ? null : password.trim();
        email = normalizedEmail;
        password = normalizedPassword;

        FluentValidator.of(this)
                .ruleFor("email", ignored -> normalizedEmail)
                .notNullOrBlank()
                .email()
                .ruleFor("password", ignored -> normalizedPassword)
                .notNullOrBlank()
                .ruleFor("deviceInfo", ignored -> deviceInfo)
                .notNull()
                .validateAndThrow();
    }
}
