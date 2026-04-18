package dev.ngb.app.identity.application.usecase.registration.register_account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequest(
        @NotBlank @Email
        String email,
        @NotBlank
        String password
) {}
