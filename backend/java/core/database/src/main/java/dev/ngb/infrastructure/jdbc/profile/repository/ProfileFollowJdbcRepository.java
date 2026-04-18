package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.graph.ProfileFollow;
import dev.ngb.domain.profile.repository.ProfileFollowRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileFollowJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileFollowJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileFollowJdbcRepository extends JdbcRepository<ProfileFollow, ProfileFollowJdbcEntity, Long> implements ProfileFollowRepository {

    public ProfileFollowJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileFollowJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileFollowJdbcMapper.INSTANCE);
    }
}
