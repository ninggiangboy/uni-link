package dev.ngb.infrastructure.jdbc.identity.mapper;

import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountOtpJdbcEntity;

public final class AccountOtpJdbcMapper implements JdbcMapper<AccountOtp, AccountOtpJdbcEntity> {

    public static final AccountOtpJdbcMapper INSTANCE = new AccountOtpJdbcMapper();

    private AccountOtpJdbcMapper() {}

    @Override
    public AccountOtp toDomain(AccountOtpJdbcEntity entity) {
        return AccountOtp.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                entity.getCode(),
                entity.getPurpose(),
                entity.getChannel(),
                entity.getExpiresAt(),
                entity.getIsUsed(),
                entity.getAttempts()
        );
    }

    @Override
    public AccountOtpJdbcEntity toJdbc(AccountOtp domain) {
        return AccountOtpJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .code(domain.getCode())
                .purpose(domain.getPurpose())
                .channel(domain.getChannel())
                .expiresAt(domain.getExpiresAt())
                .isUsed(domain.getIsUsed())
                .attempts(domain.getAttempts())
                .build();
    }
}

