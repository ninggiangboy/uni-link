package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.graph.ProfileBlock;
import dev.ngb.domain.profile.repository.ProfileBlockRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileBlockJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileBlockJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileBlockJdbcRepository extends JdbcRepository<ProfileBlock, ProfileBlockJdbcEntity, Long> implements ProfileBlockRepository {

    public ProfileBlockJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileBlockJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileBlockJdbcMapper.INSTANCE);
    }
}
