package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.repository.ProfileStatsDeltaEventDedupeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileStatsDeltaEventDedupeJdbcRepository implements ProfileStatsDeltaEventDedupeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProfileStatsDeltaEventDedupeJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean tryClaimEvent(String eventUuid) {
        int rows = jdbcTemplate.update(
                """
                        INSERT INTO prf_profile_stats_delta_processed (event_uuid)
                        VALUES (?)
                        ON CONFLICT (event_uuid) DO NOTHING
                        """,
                eventUuid
        );
        return rows > 0;
    }
}
