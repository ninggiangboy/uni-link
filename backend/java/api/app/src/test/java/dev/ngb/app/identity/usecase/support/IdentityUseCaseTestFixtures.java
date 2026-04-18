package dev.ngb.app.identity.usecase.support;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.AccountStatus;
import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.domain.identity.model.session.AccountSession;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class IdentityUseCaseTestFixtures {

    private IdentityUseCaseTestFixtures() {}

    public static final String EMAIL = "user@test.com";
    public static final String IP = "127.0.0.1";
    public static final String FINGERPRINT = "fp-test-1";

    public static DeviceInfo device() {
        return new DeviceInfo(DeviceType.WEB, "chrome", FINGERPRINT);
    }

    public static Account pendingAccount(long id) {
        return Account.reconstruct(
                id,
                "uuid-pending-" + id,
                null,
                Instant.now(),
                null,
                Instant.now(),
                EMAIL,
                null,
                "hash",
                AccountStatus.PENDING,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static Account activeAccount(long id) {
        return Account.reconstruct(
                id,
                "uuid-active-" + id,
                null,
                Instant.now(),
                null,
                Instant.now(),
                EMAIL,
                null,
                "stored-hash",
                AccountStatus.ACTIVE,
                true,
                false,
                false,
                null,
                null
        );
    }

    public static Account activeAccountWith2Fa(long id) {
        return Account.reconstruct(
                id,
                "uuid-2fa-" + id,
                null,
                Instant.now(),
                null,
                Instant.now(),
                EMAIL,
                null,
                "stored-hash",
                AccountStatus.ACTIVE,
                true,
                false,
                true,
                null,
                null
        );
    }

    public static Account accountWithStatus(long id, AccountStatus status) {
        return Account.reconstruct(
                id,
                "uuid-" + status + "-" + id,
                null,
                Instant.now(),
                null,
                Instant.now(),
                EMAIL,
                null,
                "stored-hash",
                status,
                status == AccountStatus.ACTIVE,
                false,
                false,
                null,
                null
        );
    }

    public static AccountDevice deviceRow(long id, long accountId, String fingerprint, boolean trusted) {
        return AccountDevice.reconstruct(
                id,
                "dev-uuid-" + id,
                null,
                Instant.now(),
                null,
                Instant.now(),
                accountId,
                DeviceType.WEB,
                "chrome",
                fingerprint,
                null,
                null,
                Instant.now(),
                trusted
        );
    }

    public static AccountSession validSession(long id, long accountId, long deviceId, String tokenHash) {
        return AccountSession.reconstruct(
                id,
                "sess-" + id,
                null,
                Instant.now(),
                null,
                Instant.now(),
                accountId,
                deviceId,
                tokenHash,
                IP,
                Instant.now().plus(30, ChronoUnit.DAYS),
                false
        );
    }
}
