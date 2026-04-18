package dev.ngb.app.identity.application.usecase.registration.register_account.dto;

import dev.ngb.util.validation.FluentValidator;

public record RegisterAccountRequest(
        String email,
        String password
) {
    public RegisterAccountRequest {
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
                .validateAndThrow();
    }
}
