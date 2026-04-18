package dev.ngb.domain.identity.model.auth;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a known device associated with an account, used for session tracking and push notifications.
 */
@Getter
public class AccountDevice extends DomainEntity<Long> {

    private AccountDevice() {}

    private Long accountId;
    private DeviceType deviceType;
    private String deviceName;
    private String fingerprint;
    private String userAgent;
    private String pushToken;
    private Instant lastActiveAt;
    private Boolean isTrusted;

    public static AccountDevice create(Long accountId, DeviceType deviceType, String deviceName, String fingerprint) {
        AccountDevice obj = new AccountDevice();
        obj.createdAt = Instant.now(obj.clock);
        obj.accountId = accountId;
        obj.deviceType = deviceType;
        obj.deviceName = deviceName;
        obj.fingerprint = fingerprint;
        obj.lastActiveAt = Instant.now(obj.clock);
        obj.isTrusted = false;
        return obj;
    }

    public void markTrusted() {
        this.isTrusted = true;
        this.updatedAt = Instant.now(clock);
    }

    public void touch() {
        this.lastActiveAt = Instant.now(clock);
        this.updatedAt = Instant.now(clock);
    }

    public static AccountDevice reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long accountId, DeviceType deviceType, String deviceName, String fingerprint,
            String userAgent, String pushToken, Instant lastActiveAt, Boolean isTrusted) {
        AccountDevice obj = new AccountDevice();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.accountId = accountId;
        obj.deviceType = deviceType;
        obj.deviceName = deviceName;
        obj.fingerprint = fingerprint;
        obj.userAgent = userAgent;
        obj.pushToken = pushToken;
        obj.lastActiveAt = lastActiveAt;
        obj.isTrusted = isTrusted;
        return obj;
    }
}
