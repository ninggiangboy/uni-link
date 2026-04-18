package dev.ngb.infrastructure.jdbc.identity.repository;

import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.identity.entity.AccountOtpJdbcEntity;
import dev.ngb.infrastructure.jdbc.identity.mapper.AccountOtpJdbcMapper;
import dev.ngb.util.TimeProvider;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccountOtpJdbcRepository extends JdbcRepository<AccountOtp, AccountOtpJdbcEntity, Long> implements AccountOtpRepository {

    public AccountOtpJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(AccountOtpJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AccountOtpJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<AccountOtp> findLatestActiveByAccountIdAndPurpose(Long accountId, OtpPurpose purpose) {
        Criteria criteria = Criteria.where("account_id").is(accountId)
                .and("purpose").is(purpose.name())
                .and("is_used").is(false)
                .and("expires_at").greaterThan(TimeProvider.now());
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return findFirst(criteria, sort);
    }
}
