package dev.ngb.domain.group.model.membership;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A request submitted by a profile to join a group that requires approval.
 */
@Getter
public class GroupMemberRequest extends DomainEntity<Long> {

    private GroupMemberRequest() {}

    private Long groupId;
    private Long requesterProfileId;
    private GroupMemberRequestStatus status;
    private Long reviewedByProfileId;
    private String reviewNote;

    public static GroupMemberRequest create(Long groupId, Long requesterProfileId) {
        GroupMemberRequest obj = new GroupMemberRequest();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.requesterProfileId = requesterProfileId;
        obj.status = GroupMemberRequestStatus.PENDING;
        return obj;
    }

    public static GroupMemberRequest reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, Long requesterProfileId, GroupMemberRequestStatus status,
            Long reviewedByProfileId, String reviewNote
    ) {
        GroupMemberRequest obj = new GroupMemberRequest();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.requesterProfileId = requesterProfileId;
        obj.status = status;
        obj.reviewedByProfileId = reviewedByProfileId;
        obj.reviewNote = reviewNote;
        return obj;
    }
}
