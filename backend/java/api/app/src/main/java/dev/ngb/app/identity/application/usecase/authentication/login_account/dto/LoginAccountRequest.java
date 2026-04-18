package dev.ngb.app.identity.application.usecase.authentication.login_account.dto;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginAccountRequest(
        @NotBlank @Email
        String email,
        @NotBlank
        String password,
        @NotNull @Valid
        DeviceInfo deviceInfo
) {}
