package dev.ngb.domain.identity.service;

import dev.ngb.domain.DomainService;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.DeviceType;

public class AuthenticationPolicyService implements DomainService {

    public LoginDecision decidePasswordLogin(Account account, AccountDevice existingDevice) {
        if (existingDevice == null) {
            return LoginDecision.REQUIRE_VERIFICATION_NEW_DEVICE;
        }

        if (Boolean.TRUE.equals(account.getTwoFactorEnabled())) {
            return LoginDecision.REQUIRE_VERIFICATION_2FA;
        }

        return LoginDecision.TRUSTED_DEVICE_DIRECT_AUTH;
    }

    public AccountDevice decideDeviceForLoginVerification(
            Long accountId,
            AccountDevice existingDevice,
            DeviceType deviceType,
            String deviceName,
            String fingerprint
    ) {
        if (existingDevice == null) {
            return AccountDevice.create(accountId, deviceType, deviceName, fingerprint);
        }

        existingDevice.touch();
        return existingDevice;
    }

    public AccountDevice decideDeviceForOAuthSignIn(
            Long accountId,
            AccountDevice existingDevice,
            DeviceType deviceType,
            String deviceName,
            String fingerprint
    ) {
        if (existingDevice == null) {
            AccountDevice created = AccountDevice.create(accountId, deviceType, deviceName, fingerprint);
            created.markTrusted();
            return created;
        }

        existingDevice.touch();
        return existingDevice;
    }

    public AccountDevice decideTrustedDeviceAfterEmailVerification(
            Long accountId,
            DeviceType deviceType,
            String deviceName,
            String fingerprint
    ) {
        AccountDevice created = AccountDevice.create(accountId, deviceType, deviceName, fingerprint);
        created.markTrusted();
        return created;
    }

    public void applySuccessfulLogin(Account account, AccountDevice device, String ipAddress, boolean trustDeviceIfNeeded) {
        if (trustDeviceIfNeeded && !Boolean.TRUE.equals(device.getIsTrusted())) {
            device.markTrusted();
        }
        device.touch();
        account.recordLogin(ipAddress);
    }

    public enum LoginDecision {
        REQUIRE_VERIFICATION_NEW_DEVICE,
        REQUIRE_VERIFICATION_2FA,
        TRUSTED_DEVICE_DIRECT_AUTH
    }
}
