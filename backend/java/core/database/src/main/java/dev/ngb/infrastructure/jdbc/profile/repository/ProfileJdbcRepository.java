package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileJdbcRepository extends JdbcRepository<Profile, ProfileJdbcEntity, Long> implements ProfileRepository {

    public ProfileJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileJdbcMapper.INSTANCE);
    }

    @Override
    public boolean existsByAccountId(Long accountId) {
        return existsByFieldEqual("accountId", accountId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return existsByFieldEqual("username", username);
    }
}
