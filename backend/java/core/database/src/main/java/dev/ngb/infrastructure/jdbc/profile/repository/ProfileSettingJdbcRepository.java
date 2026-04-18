package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileSettingJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileSettingJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfileSettingJdbcRepository
        extends JdbcRepository<ProfileSetting, ProfileSettingJdbcEntity, Long>
        implements ProfileSettingRepository {

    public ProfileSettingJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileSettingJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileSettingJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<ProfileSetting> findByProfileId(Long profileId) {
        return findFirstByFieldEqual("profileId", profileId);
    }
}
