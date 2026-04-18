package dev.ngb.domain.identity.model.auth;

import dev.ngb.domain.DomainEntity;
import dev.ngb.util.TimeProvider;
import lombok.Getter;

import java.time.Instant;

/**
 * Stores authentication credentials for an account, supporting multiple providers (local password, OAuth).
 */
@Getter
public class AccountCredential extends DomainEntity<Long> {

    private AccountCredential() {}

    private Long accountId;
    private AuthProvider provider;
    private String providerAccountId;
    private String accessToken;
    private String refreshToken;

    public static AccountCredential create(Long accountId, AuthProvider provider,
                                           String providerAccountId, String accessToken, String refreshToken) {
        AccountCredential obj = new AccountCredential();
        obj.createdAt = TimeProvider.now();
        obj.accountId = accountId;
        obj.provider = provider;
        obj.providerAccountId = providerAccountId;
        obj.accessToken = accessToken;
        obj.refreshToken = refreshToken;
        return obj;
    }

    public static AccountCredential reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long accountId, AuthProvider provider, String providerAccountId,
            String accessToken, String refreshToken) {
        AccountCredential obj = new AccountCredential();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.accountId = accountId;
        obj.provider = provider;
        obj.providerAccountId = providerAccountId;
        obj.accessToken = accessToken;
        obj.refreshToken = refreshToken;
        return obj;
    }
}
