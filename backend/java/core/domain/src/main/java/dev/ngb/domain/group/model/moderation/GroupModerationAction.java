package dev.ngb.domain.group.model.moderation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * An audit record of a moderation action taken against a member within a group.
 * The current ban/mute state is reflected in {@code GroupMember.status}.
 */
@Getter
public class GroupModerationAction extends DomainEntity<Long> {

    private GroupModerationAction() {}

    private Long groupId;
    private Long targetProfileId;
    private Long moderatorProfileId;
    private GroupModerationActionType actionType;
    private String reason;
    private Instant expiresAt;

    public static GroupModerationAction create(
            Long groupId,
            Long targetProfileId,
            Long moderatorProfileId,
            GroupModerationActionType actionType,
            String reason,
            Instant expiresAt
    ) {
        GroupModerationAction obj = new GroupModerationAction();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.targetProfileId = targetProfileId;
        obj.moderatorProfileId = moderatorProfileId;
        obj.actionType = actionType;
        obj.reason = reason;
        obj.expiresAt = expiresAt;
        return obj;
    }

    public static GroupModerationAction reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, Long targetProfileId, Long moderatorProfileId,
            GroupModerationActionType actionType, String reason, Instant expiresAt
    ) {
        GroupModerationAction obj = new GroupModerationAction();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.targetProfileId = targetProfileId;
        obj.moderatorProfileId = moderatorProfileId;
        obj.actionType = actionType;
        obj.reason = reason;
        obj.expiresAt = expiresAt;
        return obj;
    }
}
