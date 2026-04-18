package dev.ngb.infrastructure.jdbc.identity.repository;

import dev.ngb.domain.identity.model.auth.AccountCredential;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.domain.identity.repository.AccountCredentialRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountCredentialJdbcEntity;
import dev.ngb.infrastructure.jdbc.identity.mapper.AccountCredentialJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountCredentialJdbcRepository
        extends JdbcRepository<AccountCredential, AccountCredentialJdbcEntity, Long>
        implements AccountCredentialRepository {

    public AccountCredentialJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(AccountCredentialJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AccountCredentialJdbcMapper.INSTANCE);
    }

    @Override
    public List<AccountCredential> findByAccountId(Long accountId) {
        return findAllByFieldEqual("accountId", accountId);
    }

    @Override
    public boolean existsByAccountIdAndProvider(Long accountId, AuthProvider provider) {
        Criteria criteria = Criteria
                .where("accountId").is(accountId)
                .and("provider").is(provider.name());
        return exists(criteria);
    }
}
