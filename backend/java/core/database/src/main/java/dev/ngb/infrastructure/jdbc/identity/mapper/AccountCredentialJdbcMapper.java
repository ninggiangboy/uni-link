package dev.ngb.infrastructure.jdbc.identity.mapper;

import dev.ngb.domain.identity.model.auth.AccountCredential;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountCredentialJdbcEntity;

public final class AccountCredentialJdbcMapper implements JdbcMapper<AccountCredential, AccountCredentialJdbcEntity> {

    public static final AccountCredentialJdbcMapper INSTANCE = new AccountCredentialJdbcMapper();

    private AccountCredentialJdbcMapper() {}

    @Override
    public AccountCredential toDomain(AccountCredentialJdbcEntity entity) {
        return AccountCredential.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                entity.getProvider(),
                entity.getProviderAccountId(),
                entity.getAccessToken(),
                entity.getRefreshToken()
        );
    }

    @Override
    public AccountCredentialJdbcEntity toJdbc(AccountCredential domain) {
        return AccountCredentialJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .provider(domain.getProvider())
                .providerAccountId(domain.getProviderAccountId())
                .accessToken(domain.getAccessToken())
                .refreshToken(domain.getRefreshToken())
                .build();
    }
}

