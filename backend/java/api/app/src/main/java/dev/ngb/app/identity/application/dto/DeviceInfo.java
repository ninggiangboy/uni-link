package dev.ngb.app.identity.application.dto;

import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.util.validation.FluentValidator;

public record DeviceInfo(
        DeviceType deviceType,
        String deviceName,
        String fingerprint
) {
    public DeviceInfo {
        String normalizedDeviceName = deviceName == null ? null : deviceName.trim();
        String normalizedFingerprint = fingerprint == null ? null : fingerprint.trim();
        deviceName = normalizedDeviceName;
        fingerprint = normalizedFingerprint;

        FluentValidator.of(this)
                .ruleFor("deviceType", ignored -> deviceType)
                .notNull()
                .ruleFor("deviceName", ignored -> normalizedDeviceName)
                .notNullOrBlank()
                .ruleFor("fingerprint", ignored -> normalizedFingerprint)
                .notNullOrBlank()
                .validateAndThrow();
    }
}
