package dev.ngb.domain.identity.model.auth;

import dev.ngb.domain.DomainEntity;
import dev.ngb.domain.identity.error.AccountError;
import lombok.Getter;

import java.time.Instant;

/**
 * Core identity entity representing a user's authentication account, separated from their public profile.
 * <p>
 * Aggregate root of the identity domain. Credentials, devices, sessions, OTPs, and login history
 * are separate entities with their own repositories; they are not owned by this aggregate.
 */
@Getter
public class Account extends DomainEntity<Long> {

    private Account() {}

    private String email;
    private String phoneNumber;
    private String passwordHash;
    private AccountStatus status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean twoFactorEnabled;
    private Instant lastLoginAt;
    private String lastLoginIp;

    public static Account create(String email, String passwordHash) {
        Account obj = new Account();
        obj.createdAt = Instant.now(obj.clock);
        obj.email = email;
        obj.passwordHash = passwordHash;
        obj.status = AccountStatus.PENDING;
        obj.emailVerified = false;
        obj.phoneVerified = false;
        obj.twoFactorEnabled = false;
        return obj;
    }

    public static Account createFromOAuth(String email) {
        Account obj = new Account();
        obj.createdAt = Instant.now(obj.clock);
        obj.email = email;
        obj.status = AccountStatus.ACTIVE;
        obj.emailVerified = true;
        obj.phoneVerified = false;
        obj.twoFactorEnabled = false;
        return obj;
    }

    public void activate() {
        if (this.status != AccountStatus.PENDING) {
            throw AccountError.ACCOUNT_NOT_ACTIVE.exception();
        }
        this.status = AccountStatus.ACTIVE;
        this.emailVerified = true;
        this.updatedAt = Instant.now(clock);
    }

    public void recordLogin(String ip) {
        this.lastLoginAt = Instant.now(clock);
        this.lastLoginIp = ip;
        this.updatedAt = Instant.now(clock);
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now(clock);
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public boolean isPending() {
        return this.status == AccountStatus.PENDING;
    }

    public void ensureCanLogin() {
        switch (this.status) {
            case PENDING -> throw AccountError.ACCOUNT_PENDING.exception();
            case SUSPENDED -> throw AccountError.ACCOUNT_SUSPENDED.exception();
            case BANNED -> throw AccountError.ACCOUNT_BANNED.exception();
            case DEACTIVATED -> throw AccountError.ACCOUNT_NOT_ACTIVE.exception();
            case ACTIVE -> {
            }
        }
    }

    public static Account reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            String email, String phoneNumber, String passwordHash, AccountStatus status,
            Boolean emailVerified, Boolean phoneVerified, Boolean twoFactorEnabled,
            Instant lastLoginAt, String lastLoginIp) {
        Account obj = new Account();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.email = email;
        obj.phoneNumber = phoneNumber;
        obj.passwordHash = passwordHash;
        obj.status = status;
        obj.emailVerified = emailVerified;
        obj.phoneVerified = phoneVerified;
        obj.twoFactorEnabled = twoFactorEnabled;
        obj.lastLoginAt = lastLoginAt;
        obj.lastLoginIp = lastLoginIp;
        return obj;
    }
}
