package dev.ngb.domain.group.model.membership;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * An invitation sent by a group member to a profile to join the group.
 */
@Getter
public class GroupInvitation extends DomainEntity<Long> {

    private GroupInvitation() {}

    private Long groupId;
    private Long inviterProfileId;
    private Long inviteeProfileId;
    private GroupInvitationStatus status;
    private String token;
    private Instant expiresAt;

    public static GroupInvitation create(
            Long groupId,
            Long inviterProfileId,
            Long inviteeProfileId,
            Instant expiresAt
    ) {
        GroupInvitation obj = new GroupInvitation();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.inviterProfileId = inviterProfileId;
        obj.inviteeProfileId = inviteeProfileId;
        obj.status = GroupInvitationStatus.PENDING;
        obj.token = UUID.randomUUID().toString();
        obj.expiresAt = expiresAt;
        return obj;
    }

    public static GroupInvitation reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, Long inviterProfileId, Long inviteeProfileId,
            GroupInvitationStatus status, String token, Instant expiresAt
    ) {
        GroupInvitation obj = new GroupInvitation();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.inviterProfileId = inviterProfileId;
        obj.inviteeProfileId = inviteeProfileId;
        obj.status = status;
        obj.token = token;
        obj.expiresAt = expiresAt;
        return obj;
    }
}
