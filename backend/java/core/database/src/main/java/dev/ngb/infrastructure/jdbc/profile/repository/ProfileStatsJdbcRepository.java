package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileStatsJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileStatsJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProfileStatsJdbcRepository extends JdbcRepository<ProfileStats, ProfileStatsJdbcEntity, Long>
        implements ProfileStatsRepository {

    public ProfileStatsJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(ProfileStatsJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, ProfileStatsJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<ProfileStats> findByProfileId(Long profileId) {
        return findFirstByFieldEqual("profile_id", profileId);
    }

    @Override
    public void adjustFollowerCount(long profileId, long delta) {
        if (delta == 0) {
            return;
        }
        jdbcTemplate.update(
                """
                        UPDATE prf_profile_stats
                        SET follower_count = GREATEST(0, follower_count + ?),
                            updated_at = NOW()
                        WHERE profile_id = ?
                        """,
                delta,
                profileId
        );
    }

    @Override
    public void adjustFollowingCount(long profileId, long delta) {
        if (delta == 0) {
            return;
        }
        jdbcTemplate.update(
                """
                        UPDATE prf_profile_stats
                        SET following_count = GREATEST(0, following_count + ?),
                            updated_at = NOW()
                        WHERE profile_id = ?
                        """,
                delta,
                profileId
        );
    }
}

