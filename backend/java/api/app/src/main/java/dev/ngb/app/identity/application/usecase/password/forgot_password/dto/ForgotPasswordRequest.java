package dev.ngb.app.identity.application.usecase.password.forgot_password.dto;

import dev.ngb.util.validation.FluentValidator;

public record ForgotPasswordRequest(
        String email
) {
    public ForgotPasswordRequest {
        String normalizedEmail = email == null ? null : email.trim();
        email = normalizedEmail;

        FluentValidator.of(this)
                .ruleFor("email", ignored -> normalizedEmail)
                .notNullOrBlank()
                .email()
                .validateAndThrow();
    }
}
