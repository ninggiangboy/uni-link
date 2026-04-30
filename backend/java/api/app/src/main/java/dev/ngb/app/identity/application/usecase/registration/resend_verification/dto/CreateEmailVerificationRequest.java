package dev.ngb.app.identity.application.usecase.registration.resend_verification.dto;

import dev.ngb.util.validation.FluentValidator;

public record CreateEmailVerificationRequest(
        String email
) {
    public CreateEmailVerificationRequest {
        String normalizedEmail = email == null ? null : email.trim();
        email = normalizedEmail;

        FluentValidator.of(this)
                .ruleFor("email", ignored -> normalizedEmail)
                .notNullOrBlank()
                .email()
                .validateAndThrow();
    }
}
