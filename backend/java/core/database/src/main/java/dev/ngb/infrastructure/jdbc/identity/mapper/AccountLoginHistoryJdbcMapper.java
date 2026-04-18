package dev.ngb.infrastructure.jdbc.identity.mapper;

import dev.ngb.domain.identity.model.session.AccountLoginHistory;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountLoginHistoryJdbcEntity;

public final class AccountLoginHistoryJdbcMapper implements JdbcMapper<AccountLoginHistory, AccountLoginHistoryJdbcEntity> {

    public static final AccountLoginHistoryJdbcMapper INSTANCE = new AccountLoginHistoryJdbcMapper();

    private AccountLoginHistoryJdbcMapper() {}

    @Override
    public AccountLoginHistory toDomain(AccountLoginHistoryJdbcEntity entity) {
        return AccountLoginHistory.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                entity.getDeviceId(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getResult(),
                entity.getFailureReason()
        );
    }

    @Override
    public AccountLoginHistoryJdbcEntity toJdbc(AccountLoginHistory domain) {
        return AccountLoginHistoryJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .deviceId(domain.getDeviceId())
                .ipAddress(domain.getIpAddress())
                .userAgent(domain.getUserAgent())
                .result(domain.getResult())
                .failureReason(domain.getFailureReason())
                .build();
    }
}

