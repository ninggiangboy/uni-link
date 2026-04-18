package dev.ngb.domain.identity.model.otp;

import dev.ngb.domain.DomainEntity;
import dev.ngb.domain.identity.error.AccountError;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

/**
 * One-time password issued for account verification, login, password reset, or two-factor authentication.
 */
@Getter
public class AccountOtp extends DomainEntity<Long> {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    private AccountOtp() {}

    private Long accountId;
    private String code;
    private OtpPurpose purpose;
    private OtpChannel channel;
    private Instant expiresAt;
    private Boolean isUsed;
    private Integer attempts;

    public static AccountOtp create(Long accountId, String code, OtpPurpose purpose, OtpChannel channel) {
        AccountOtp obj = new AccountOtp();
        obj.createdAt = Instant.now(obj.clock);
        obj.accountId = accountId;
        obj.code = code;
        obj.purpose = purpose;
        obj.channel = channel;
        obj.expiresAt = Instant.now(obj.clock).plus(DEFAULT_TTL);
        obj.isUsed = false;
        obj.attempts = 0;
        return obj;
    }

    public void verify(String inputCode) {
        if (this.isUsed) {
            throw AccountError.INVALID_OTP.exception();
        }
        if (Instant.now(clock).isAfter(this.expiresAt)) {
            throw AccountError.INVALID_OTP.exception();
        }
        this.attempts++;
        if (this.attempts > MAX_ATTEMPTS) {
            throw AccountError.OTP_MAX_ATTEMPTS.exception();
        }
        if (!this.code.equals(inputCode)) {
            throw AccountError.INVALID_OTP.exception();
        }
        this.isUsed = true;
        this.updatedAt = Instant.now(clock);
    }

    public boolean isExpired() {
        return Instant.now(clock).isAfter(this.expiresAt);
    }

    public static AccountOtp reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long accountId, String code, OtpPurpose purpose, OtpChannel channel,
            Instant expiresAt, Boolean isUsed, Integer attempts) {
        AccountOtp obj = new AccountOtp();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.accountId = accountId;
        obj.code = code;
        obj.purpose = purpose;
        obj.channel = channel;
        obj.expiresAt = expiresAt;
        obj.isUsed = isUsed;
        obj.attempts = attempts;
        return obj;
    }
}
