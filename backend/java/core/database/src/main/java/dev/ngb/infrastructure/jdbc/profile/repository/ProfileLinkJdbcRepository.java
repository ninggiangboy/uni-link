package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileLinkJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileLinkJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProfileLinkJdbcRepository
        extends JdbcRepository<ProfileLink, ProfileLinkJdbcEntity, Long>
        implements ProfileLinkRepository {

    public ProfileLinkJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileLinkJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileLinkJdbcMapper.INSTANCE);
    }

    @Override
    public List<ProfileLink> findByProfileId(Long profileId) {
        return findAllByFieldEqual("profileId", profileId);
    }
}
