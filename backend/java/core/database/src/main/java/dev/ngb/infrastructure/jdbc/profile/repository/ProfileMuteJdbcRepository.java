package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.graph.ProfileMute;
import dev.ngb.domain.profile.repository.ProfileMuteRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMuteJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileMuteJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileMuteJdbcRepository extends JdbcRepository<ProfileMute, ProfileMuteJdbcEntity, Long> implements ProfileMuteRepository {

    public ProfileMuteJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileMuteJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileMuteJdbcMapper.INSTANCE);
    }
}
