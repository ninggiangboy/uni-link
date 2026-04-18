package dev.ngb.domain.identity.model.session;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Audit trail recording every login attempt for an account, including device and result.
 */
@Getter
public class AccountLoginHistory extends DomainEntity<Long> {

    private AccountLoginHistory() {}

    private Long accountId;
    private Long deviceId;
    private String ipAddress;
    private String userAgent;
    private LoginResult result;
    private String failureReason;

    public static AccountLoginHistory createSuccess(Long accountId, Long deviceId, String ipAddress,
                                                    String userAgent) {
        AccountLoginHistory obj = new AccountLoginHistory();
        obj.createdAt = Instant.now(obj.clock);
        obj.accountId = accountId;
        obj.deviceId = deviceId;
        obj.ipAddress = ipAddress;
        obj.userAgent = userAgent;
        obj.result = LoginResult.SUCCESS;
        obj.failureReason = null;
        return obj;
    }

    public static AccountLoginHistory createFailure(Long accountId, Long deviceId, String ipAddress,
                                                    String userAgent, String failureReason) {
        AccountLoginHistory obj = new AccountLoginHistory();
        obj.createdAt = Instant.now(obj.clock);
        obj.accountId = accountId;
        obj.deviceId = deviceId;
        obj.ipAddress = ipAddress;
        obj.userAgent = userAgent;
        obj.result = LoginResult.FAILED;
        obj.failureReason = failureReason;
        return obj;
    }

    public static AccountLoginHistory reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long accountId, Long deviceId, String ipAddress, String userAgent,
            LoginResult result, String failureReason) {
        AccountLoginHistory obj = new AccountLoginHistory();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.accountId = accountId;
        obj.deviceId = deviceId;
        obj.ipAddress = ipAddress;
        obj.userAgent = userAgent;
        obj.result = result;
        obj.failureReason = failureReason;
        return obj;
    }
}
