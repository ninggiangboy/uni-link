package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.model.stats.ProfileStatsCountDelta;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.ProfileStatsJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.ProfileStatsJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProfileStatsJdbcRepository extends JdbcRepository<ProfileStats, ProfileStatsJdbcEntity, Long>
        implements ProfileStatsRepository {

    private static final int BULK_VALUES_CHUNK_SIZE = 500;

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
        adjustCountsBulk(List.of(new ProfileStatsCountDelta(profileId, delta, 0L)));
    }

    @Override
    public void adjustFollowingCount(long profileId, long delta) {
        if (delta == 0) {
            return;
        }
        adjustCountsBulk(List.of(new ProfileStatsCountDelta(profileId, 0L, delta)));
    }

    @Override
    public void adjustCountsBulk(List<ProfileStatsCountDelta> adjustments) {
        if (adjustments == null || adjustments.isEmpty()) {
            return;
        }
        List<ProfileStatsCountDelta> nonZero = new ArrayList<>(adjustments.size());
        for (ProfileStatsCountDelta d : adjustments) {
            if (d.followerDelta() != 0L || d.followingDelta() != 0L) {
                nonZero.add(d);
            }
        }
        if (nonZero.isEmpty()) {
            return;
        }
        for (int from = 0; from < nonZero.size(); from += BULK_VALUES_CHUNK_SIZE) {
            int to = Math.min(from + BULK_VALUES_CHUNK_SIZE, nonZero.size());
            adjustCountsBulkChunk(nonZero.subList(from, to));
        }
    }

    private void adjustCountsBulkChunk(List<ProfileStatsCountDelta> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        StringBuilder sql = new StringBuilder(
                """
                        UPDATE prf_profile_stats s
                        SET follower_count = GREATEST(0, s.follower_count + d.follower_delta),
                            following_count = GREATEST(0, s.following_count + d.following_delta),
                            updated_at = NOW()
                        FROM (VALUES \
                        """
        );
        for (int i = 0; i < chunk.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(?, ?, ?)");
        }
        sql.append(
                """
                        ) AS d(profile_id, follower_delta, following_delta)
                        WHERE s.profile_id = d.profile_id
                        """
        );
        Object[] args = new Object[chunk.size() * 3];
        int a = 0;
        for (ProfileStatsCountDelta d : chunk) {
            args[a++] = d.profileId();
            args[a++] = d.followerDelta();
            args[a++] = d.followingDelta();
        }
        jdbcTemplate.update(sql.toString(), args);
    }
}
