package dev.ngb.infrastructure.jdbc.identity.mapper;

import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountSessionJdbcEntity;

public final class AccountSessionJdbcMapper implements JdbcMapper<AccountSession, AccountSessionJdbcEntity> {

    public static final AccountSessionJdbcMapper INSTANCE = new AccountSessionJdbcMapper();

    private AccountSessionJdbcMapper() {}

    @Override
    public AccountSession toDomain(AccountSessionJdbcEntity entity) {
        return AccountSession.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                entity.getDeviceId(),
                entity.getTokenHash(),
                entity.getIpAddress(),
                entity.getExpiresAt(),
                entity.getIsRevoked()
        );
    }

    @Override
    public AccountSessionJdbcEntity toJdbc(AccountSession domain) {
        return AccountSessionJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .deviceId(domain.getDeviceId())
                .tokenHash(domain.getTokenHash())
                .ipAddress(domain.getIpAddress())
                .expiresAt(domain.getExpiresAt())
                .isRevoked(domain.getIsRevoked())
                .build();
    }
}

