package dev.ngb.infrastructure.jdbc.identity.mapper;

import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountJdbcEntity;

public final class AccountJdbcMapper implements JdbcMapper<Account, AccountJdbcEntity> {

    public static final AccountJdbcMapper INSTANCE = new AccountJdbcMapper();

    private AccountJdbcMapper() {}

    @Override
    public Account toDomain(AccountJdbcEntity entity) {
        return Account.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getPasswordHash(),
                entity.getStatus(),
                entity.getEmailVerified(),
                entity.getPhoneVerified(),
                entity.getTwoFactorEnabled(),
                entity.getLastLoginAt(),
                entity.getLastLoginIp()
        );
    }

    @Override
    public AccountJdbcEntity toJdbc(Account domain) {
        return AccountJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .email(domain.getEmail())
                .phoneNumber(domain.getPhoneNumber())
                .passwordHash(domain.getPasswordHash())
                .status(domain.getStatus())
                .emailVerified(domain.getEmailVerified())
                .phoneVerified(domain.getPhoneVerified())
                .twoFactorEnabled(domain.getTwoFactorEnabled())
                .lastLoginAt(domain.getLastLoginAt())
                .lastLoginIp(domain.getLastLoginIp())
                .build();
    }
}

