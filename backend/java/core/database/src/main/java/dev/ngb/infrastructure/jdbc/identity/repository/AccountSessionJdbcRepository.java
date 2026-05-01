package dev.ngb.infrastructure.jdbc.identity.repository;

import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountSessionJdbcEntity;
import dev.ngb.infrastructure.jdbc.identity.mapper.AccountSessionJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountSessionJdbcRepository extends JdbcRepository<AccountSession, AccountSessionJdbcEntity, Long> implements AccountSessionRepository {

    public AccountSessionJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(AccountSessionJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AccountSessionJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<AccountSession> findByTokenHash(String tokenHash) {
        return findFirstByFieldEqual("token_hash", tokenHash);
    }

    @Override
    public List<AccountSession> findActiveByAccountId(Long accountId) {
        Criteria criteria = Criteria.where("account_id").is(accountId)
                .and("is_revoked").is(false);
        return findAll(criteria);
    }

    @Override
    public boolean revokeIfActiveByTokenHash(String tokenHash, Instant now) {
        Timestamp nowTimestamp = Timestamp.from(now);
        int affected = jdbcTemplate.update(
                """
                UPDATE iam_account_sessions
                SET is_revoked = TRUE, updated_at = ?
                WHERE token_hash = ?
                  AND is_revoked = FALSE
                  AND expires_at > ?
                """,
                nowTimestamp,
                tokenHash,
                nowTimestamp
        );
        return affected > 0;
    }
}
