package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.profile.ProfileMetadata;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMetadataJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileMetadataJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileMetadataJdbcRepository
        extends JdbcRepository<ProfileMetadata, ProfileMetadataJdbcEntity, Long>
        implements ProfileMetadataRepository {

    public ProfileMetadataJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileMetadataJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileMetadataJdbcMapper.INSTANCE);
    }

    @Override
    public List<ProfileMetadata> findByProfileId(Long profileId) {
        return findAllByFieldEqual("profileId", profileId);
    }
}
