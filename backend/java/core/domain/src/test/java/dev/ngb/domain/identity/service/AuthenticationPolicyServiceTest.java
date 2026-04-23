package dev.ngb.domain.identity.service;

import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.AccountStatus;
import dev.ngb.domain.identity.model.auth.DeviceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationPolicyServiceTest {

    private final AuthenticationPolicyService service = new AuthenticationPolicyService();

    @Test
    @DisplayName("New device requires verification")
    void decidePasswordLoginWhenDeviceMissingRequiresVerification() {
        Account account = account(false);

        var decision = service.decidePasswordLogin(account, null);

        assertThat(decision).isEqualTo(AuthenticationPolicyService.LoginDecision.REQUIRE_VERIFICATION_NEW_DEVICE);
    }

    @Test
    @DisplayName("2FA on known device requires verification")
    void decidePasswordLoginWhen2faEnabledRequiresVerification() {
        Account account = account(true);
        AccountDevice device = AccountDevice.create(1L, DeviceType.WEB, "chrome", "fp");

        var decision = service.decidePasswordLogin(account, device);

        assertThat(decision).isEqualTo(AuthenticationPolicyService.LoginDecision.REQUIRE_VERIFICATION_2FA);
    }

    @Test
    @DisplayName("Known device and no 2FA allows direct auth")
    void decidePasswordLoginWhenTrustedPathReturnsDirectAuth() {
        Account account = account(false);
        AccountDevice device = AccountDevice.create(1L, DeviceType.WEB, "chrome", "fp");

        var decision = service.decidePasswordLogin(account, device);

        assertThat(decision).isEqualTo(AuthenticationPolicyService.LoginDecision.TRUSTED_DEVICE_DIRECT_AUTH);
    }

    @Test
    @DisplayName("Successful login can trust untrusted device")
    void applySuccessfulLoginTrustsAndUpdatesAggregateState() {
        Account account = account(false);
        AccountDevice device = AccountDevice.create(1L, DeviceType.WEB, "chrome", "fp");

        service.applySuccessfulLogin(account, device, "127.0.0.1", true);

        assertThat(device.getIsTrusted()).isTrue();
        assertThat(account.getLastLoginIp()).isEqualTo("127.0.0.1");
        assertThat(account.getLastLoginAt()).isNotNull();
    }

    private static Account account(boolean twoFactorEnabled) {
        return Account.reconstruct(
                1L,
                "uuid-1",
                null,
                Instant.now(),
                null,
                Instant.now(),
                "user@test.com",
                null,
                "hash",
                AccountStatus.ACTIVE,
                true,
                false,
                twoFactorEnabled,
                null,
                null
        );
    }
}
