package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.infrastructure.jdbc.base.helper.SqlConstraintViolationMapper;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileJdbcMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfileJdbcRepository extends JdbcRepository<Profile, ProfileJdbcEntity, Long> implements ProfileRepository {

    private static final String USERNAME_UNIQUE_CONSTRAINT = "uq_prf_profiles_username";
    private static final String ACCOUNT_ID_UNIQUE_CONSTRAINT = "uq_prf_profiles_account_id";

    private final SqlConstraintViolationMapper sqlConstraintViolationMapper;

    public ProfileJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate,
            SqlConstraintViolationMapper sqlConstraintViolationMapper
    ) {
        super(ProfileJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileJdbcMapper.INSTANCE);
        this.sqlConstraintViolationMapper = sqlConstraintViolationMapper;
    }

    @Override
    public Profile save(Profile entity) {
        try {
            return super.save(entity);
        } catch (DataIntegrityViolationException ex) {
            if (sqlConstraintViolationMapper.matchesConstraint(ex, ACCOUNT_ID_UNIQUE_CONSTRAINT)) {
                throw ProfileError.PROFILE_ALREADY_EXISTS_FOR_ACCOUNT.exception();
            }
            if (sqlConstraintViolationMapper.matchesConstraint(ex, USERNAME_UNIQUE_CONSTRAINT)) {
                throw ProfileError.USERNAME_ALREADY_EXISTS.exception();
            }
            throw ex;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return existsByFieldEqual("username", username);
    }

    @Override
    public boolean existsByAccountId(Long accountId) {
        return existsByFieldEqual("account_id", accountId);
    }

    @Override
    public Optional<Profile> findByUsername(String username) {
        return findFirstByFieldEqual("username", username);
    }

    @Override
    public Optional<Profile> findByAccountId(Long accountId) {
        return findFirstByFieldEqual("account_id", accountId);
    }
}
