package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.profile.ProfileMedia;
import dev.ngb.domain.profile.model.profile.ProfileMediaType;
import dev.ngb.domain.profile.repository.ProfileMediaRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileMediaJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileMediaJdbcMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return findAllByFieldEqual("profile_id", profileId);
    }

    @Override
    public Optional<ProfileMedia> findLatestByProfileIdAndType(Long profileId, ProfileMediaType type) {
        return findFirst(
                Criteria.where("profile_id").is(profileId).and("type").is(type),
                Sort.by(Sort.Direction.DESC, "created_at")
        );
    }
}
