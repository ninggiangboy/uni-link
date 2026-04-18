package dev.ngb.infrastructure.jdbc.identity.repository;

import dev.ngb.domain.identity.model.session.AccountLoginHistory;
import dev.ngb.domain.identity.repository.AccountLoginHistoryRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountLoginHistoryJdbcEntity;
import dev.ngb.infrastructure.jdbc.identity.mapper.AccountLoginHistoryJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class AccountLoginHistoryJdbcRepository extends JdbcRepository<AccountLoginHistory, AccountLoginHistoryJdbcEntity, Long> implements AccountLoginHistoryRepository {

    public AccountLoginHistoryJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(AccountLoginHistoryJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AccountLoginHistoryJdbcMapper.INSTANCE);
    }
}
