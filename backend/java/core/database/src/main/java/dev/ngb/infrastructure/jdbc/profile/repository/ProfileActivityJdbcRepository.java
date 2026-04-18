package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.activity.ProfileActivity;
import dev.ngb.domain.profile.repository.ProfileActivityRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileActivityJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileActivityJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfileActivityJdbcRepository
        extends JdbcRepository<ProfileActivity, ProfileActivityJdbcEntity, Long>
        implements ProfileActivityRepository {

    public ProfileActivityJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileActivityJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileActivityJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<ProfileActivity> findByProfileId(Long profileId) {
        return findFirstByFieldEqual("profileId", profileId);
    }
}
