package dev.ngb.infrastructure.jdbc.profile.repository;

import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.model.relationship.FollowRequestStatus;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.profile.entity.FollowRequestJdbcEntity;
import dev.ngb.infrastructure.jdbc.profile.mapper.FollowRequestJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FollowRequestJdbcRepository
        extends JdbcRepository<FollowRequest, FollowRequestJdbcEntity, Long>
        implements FollowRequestRepository {

    private static final String FIND_PENDING_BY_TARGET_SQL = """
            SELECT * FROM prf_follow_requests
             WHERE target_profile_id = :targetProfileId
               AND status = 'PENDING'
             ORDER BY created_at DESC, id DESC
             LIMIT :limit OFFSET :offset
            """;

    public FollowRequestJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(FollowRequestJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, FollowRequestJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<FollowRequest> findByUuidAndTargetProfileId(String uuid, Long targetProfileId) {
        return findFirst(Criteria.where("uuid").is(uuid).and("target_profile_id").is(targetProfileId));
    }

    @Override
    public Optional<FollowRequest> findPendingForPair(Long requesterProfileId, Long targetProfileId) {
        return findFirst(Criteria.where("requester_profile_id").is(requesterProfileId)
                .and("target_profile_id").is(targetProfileId)
                .and("status").is(FollowRequestStatus.PENDING));
    }

    @Override
    public List<FollowRequest> findPendingByTargetProfileId(Long targetProfileId, int limit, int offset) {
        return findAllBySql(FIND_PENDING_BY_TARGET_SQL, Map.of(
                "targetProfileId", targetProfileId,
                "limit", Math.max(1, limit),
                "offset", Math.max(0, offset)
        ));
    }

    @Override
    public boolean existsPending(Long requesterProfileId, Long targetProfileId) {
        return exists(Criteria.where("requester_profile_id").is(requesterProfileId)
                .and("target_profile_id").is(targetProfileId)
                .and("status").is(FollowRequestStatus.PENDING));
    }
}
