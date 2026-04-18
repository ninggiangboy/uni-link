package dev.ngb.app.identity.application.usecase.registration.verify_email.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyEmailRequest(
        @NotBlank @Email
        String email,
        @NotBlank
        String otpCode,
        @NotNull @Valid
        DeviceInfo deviceInfo
) {}
