package dev.ngb.app.identity.application.dto;

import dev.ngb.domain.identity.model.auth.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceInfo(
        @NotNull
        DeviceType deviceType,
        @NotBlank
        String deviceName,
        @NotBlank
        String fingerprint
) {}
