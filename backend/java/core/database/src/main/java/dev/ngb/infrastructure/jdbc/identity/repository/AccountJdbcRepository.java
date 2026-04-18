package dev.ngb.infrastructure.jdbc.identity.repository;

import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.infrastructure.jdbc.base.helper.SqlConstraintViolationMapper;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountJdbcEntity;
import dev.ngb.infrastructure.jdbc.identity.mapper.AccountJdbcMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountJdbcRepository extends JdbcRepository<Account, AccountJdbcEntity, Long> implements AccountRepository {

    private static final String EMAIL_UNIQUE_CONSTRAINT = "uq_iam_accounts_email";

    private final SqlConstraintViolationMapper sqlConstraintViolationMapper;

    public AccountJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate,
            SqlConstraintViolationMapper sqlConstraintViolationMapper
    ) {
        super(AccountJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AccountJdbcMapper.INSTANCE);
        this.sqlConstraintViolationMapper = sqlConstraintViolationMapper;
    }

    @Override
    public boolean existsByEmail(String email) {
        return existsByFieldEqual("email", email);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return findFirstByFieldEqual("email", email);
    }

    @Override
    public Account save(Account entity) {
        try {
            return super.save(entity);
        } catch (DataIntegrityViolationException ex) {
            if (sqlConstraintViolationMapper.matchesConstraint(ex, EMAIL_UNIQUE_CONSTRAINT)) {
                throw AccountError.EMAIL_ALREADY_EXISTS.exception();
            }
            throw ex;
        }
    }
}
