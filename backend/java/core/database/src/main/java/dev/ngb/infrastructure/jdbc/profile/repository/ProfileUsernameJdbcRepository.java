package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.username.ProfileUsername;
import dev.ngb.domain.profile.repository.ProfileUsernameRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileUsernameJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileUsernameJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProfileUsernameJdbcRepository
        extends JdbcRepository<ProfileUsername, ProfileUsernameJdbcEntity, Long>
        implements ProfileUsernameRepository {

    public ProfileUsernameJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileUsernameJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileUsernameJdbcMapper.INSTANCE);
    }

    @Override
    public List<ProfileUsername> findByProfileId(Long profileId) {
        return findAllByFieldEqual("profile_id", profileId);
    }

    @Override
    public Optional<ProfileUsername> findCurrentByProfileId(Long profileId) {
        return findFirst(Criteria.where("profile_id").is(profileId).and("is_current").isTrue());
    }
}
