package dev.ngb.app.identity.application.usecase.authentication.verify_login.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyLoginRequest(
        @NotBlank
        String verificationToken,
        @NotBlank
        String otpCode
) {}
