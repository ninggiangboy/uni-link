package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.relationship.FollowRequest;

import java.util.List;
import java.util.Optional;

/**
 * Persistence for {@link FollowRequest} aggregates.
 */
public interface FollowRequestRepository extends Repository<FollowRequest, Long> {

    Optional<FollowRequest> findByUuidAndTargetProfileId(String uuid, Long targetProfileId);

    Optional<FollowRequest> findPendingForPair(Long requesterProfileId, Long targetProfileId);

    List<FollowRequest> findPendingByTargetProfileId(Long targetProfileId, int limit, int offset);

    boolean existsPending(Long requesterProfileId, Long targetProfileId);
}
