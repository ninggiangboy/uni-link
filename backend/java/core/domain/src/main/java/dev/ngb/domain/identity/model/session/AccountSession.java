package dev.ngb.domain.identity.model.session;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

/**
 * Active login session tied to an account and device, with refresh token lifecycle.
 */
@Getter
public class AccountSession extends DomainEntity<Long> {

    private static final Duration DEFAULT_TTL = Duration.ofDays(30);

    private AccountSession() {}

    private Long accountId;
    private Long deviceId;
    private String tokenHash;
    private String ipAddress;
    private Instant expiresAt;
    private Boolean isRevoked;

    public static AccountSession create(Long accountId, Long deviceId, String tokenHash, String ipAddress) {
        AccountSession obj = new AccountSession();
        obj.createdAt = Instant.now(obj.clock);
        obj.accountId = accountId;
        obj.deviceId = deviceId;
        obj.tokenHash = tokenHash;
        obj.ipAddress = ipAddress;
        obj.expiresAt = Instant.now(obj.clock).plus(DEFAULT_TTL);
        obj.isRevoked = false;
        return obj;
    }

    public void revoke() {
        this.isRevoked = true;
        this.updatedAt = Instant.now(clock);
    }

    public boolean isValid() {
        return !this.isRevoked && Instant.now(clock).isBefore(this.expiresAt);
    }

    public static AccountSession reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long accountId, Long deviceId, String tokenHash, String ipAddress,
            Instant expiresAt, Boolean isRevoked) {
        AccountSession obj = new AccountSession();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.accountId = accountId;
        obj.deviceId = deviceId;
        obj.tokenHash = tokenHash;
        obj.ipAddress = ipAddress;
        obj.expiresAt = expiresAt;
        obj.isRevoked = isRevoked;
        return obj;
    }
}
