package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.membership.GroupMemberRequest;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link GroupMemberRequest} entities.
 */
public interface GroupMemberRequestRepository extends Repository<GroupMemberRequest, Long> {

    Optional<GroupMemberRequest> findByGroupIdAndRequesterProfileId(Long groupId, Long requesterProfileId);

    List<GroupMemberRequest> findByGroupId(Long groupId);

    List<GroupMemberRequest> findByRequesterProfileId(Long requesterProfileId);

    boolean existsByGroupIdAndRequesterProfileId(Long groupId, Long requesterProfileId);
}
