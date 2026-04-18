package dev.ngb.infrastructure.jdbc.identity.repository;

import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountDeviceJdbcEntity;
import dev.ngb.infrastructure.jdbc.identity.mapper.AccountDeviceJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class AccountDeviceJdbcRepository
        extends JdbcRepository<AccountDevice, AccountDeviceJdbcEntity, Long>
        implements AccountDeviceRepository {

    public AccountDeviceJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(AccountDeviceJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AccountDeviceJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<AccountDevice> findByAccountIdAndFingerprint(Long accountId, String fingerprint) {
        String sql = """
                SELECT *
                FROM iam_account_devices
                WHERE account_id = :accountId
                  AND fingerprint = :fingerprint
                """;
        return findOneBySql(sql, Map.of(
                "accountId", accountId,
                "fingerprint", fingerprint
        ));
    }
}

