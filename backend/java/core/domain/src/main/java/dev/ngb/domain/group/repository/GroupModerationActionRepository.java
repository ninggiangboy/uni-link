package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.moderation.GroupModerationAction;

import java.util.List;

/**
 * Repository for managing {@link GroupModerationAction} audit records.
 */
public interface GroupModerationActionRepository extends Repository<GroupModerationAction, Long> {

    List<GroupModerationAction> findByGroupId(Long groupId);

    List<GroupModerationAction> findByGroupIdAndTargetProfileId(Long groupId, Long targetProfileId);
}
