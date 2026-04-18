package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.profile.ProfileMedia;
import dev.ngb.domain.profile.repository.ProfileMediaRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMediaJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileMediaJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileMediaJdbcRepository
        extends JdbcRepository<ProfileMedia, ProfileMediaJdbcEntity, Long>
        implements ProfileMediaRepository {

    public ProfileMediaJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileMediaJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileMediaJdbcMapper.INSTANCE);
    }

    @Override
    public List<ProfileMedia> findByProfileId(Long profileId) {
        return findAllByFieldEqual("profileId", profileId);
    }
}
