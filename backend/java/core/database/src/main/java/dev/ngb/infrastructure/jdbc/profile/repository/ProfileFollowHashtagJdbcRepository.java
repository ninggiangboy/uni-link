package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.graph.ProfileFollowHashtag;
import dev.ngb.domain.profile.repository.ProfileFollowHashtagRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileFollowHashtagJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileFollowHashtagJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileFollowHashtagJdbcRepository extends JdbcRepository<ProfileFollowHashtag, ProfileFollowHashtagJdbcEntity, Long> implements ProfileFollowHashtagRepository {

    public ProfileFollowHashtagJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileFollowHashtagJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileFollowHashtagJdbcMapper.INSTANCE);
    }
}
