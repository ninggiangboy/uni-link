package dev.ngb.infrastructure.jdbc.identity.mapper;

import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountDeviceJdbcEntity;

public final class AccountDeviceJdbcMapper implements JdbcMapper<AccountDevice, AccountDeviceJdbcEntity> {

    public static final AccountDeviceJdbcMapper INSTANCE = new AccountDeviceJdbcMapper();

    private AccountDeviceJdbcMapper() {}

    @Override
    public AccountDevice toDomain(AccountDeviceJdbcEntity entity) {
        return AccountDevice.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                entity.getDeviceType(),
                entity.getDeviceName(),
                entity.getFingerprint(),
                entity.getUserAgent(),
                entity.getPushToken(),
                entity.getLastActiveAt(),
                entity.getIsTrusted()
        );
    }

    @Override
    public AccountDeviceJdbcEntity toJdbc(AccountDevice domain) {
        return AccountDeviceJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .deviceType(domain.getDeviceType())
                .deviceName(domain.getDeviceName())
                .fingerprint(domain.getFingerprint())
                .userAgent(domain.getUserAgent())
                .pushToken(domain.getPushToken())
                .lastActiveAt(domain.getLastActiveAt())
                .isTrusted(domain.getIsTrusted())
                .build();
    }
}

