package dev.ngb.app.identity.application.usecase.password.reset_password.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank @Email
        String email,
        @NotBlank
        String otpCode,
        @NotBlank
        String newPassword
) {}
