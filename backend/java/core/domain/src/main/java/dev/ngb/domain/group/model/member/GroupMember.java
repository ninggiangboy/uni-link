package dev.ngb.domain.group.model.member;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile's membership within a group, including their role,
 * standing, and fine-grained admin permissions.
 * <p>
 * Permission rules:
 * <ul>
 *   <li>OWNER — implicitly holds every permission; the stored {@code permissions}
 *       value is ignored for authorization checks.</li>
 *   <li>ADMIN — holds only the permissions encoded in the {@code permissions}
 *       bitmask. Permissions can be granted or revoked individually.</li>
 *   <li>MEMBER — holds no permissions; {@code permissions} is always {@code 0}.</li>
 * </ul>
 */
@Getter
public class GroupMember extends DomainEntity<Long> {

    private GroupMember() {}

    private Long groupId;
    private Long profileId;
    private GroupMemberRole role;
    private GroupMemberStatus status;
    private Instant mutedUntil;

    /**
     * Bitmask of {@link GroupAdminPermission} bits granted to this member.
     * Only meaningful when {@code role == ADMIN}.
     */
    private Long permissions;

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Creates the initial owner membership with all permissions.
     */
    public static GroupMember createOwner(Long groupId, Long profileId) {
        GroupMember obj = new GroupMember();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.profileId = profileId;
        obj.role = GroupMemberRole.OWNER;
        obj.status = GroupMemberStatus.ACTIVE;
        obj.permissions = GroupAdminPermission.ALL;
        return obj;
    }

    /**
     * Promotes a profile to ADMIN with a custom permission bitmask.
     * Use {@link GroupAdminPermission#toBitmask} to build the value,
     * or {@link GroupAdminPermission#ALL} to grant full admin access.
     */
    public static GroupMember createAdmin(Long groupId, Long profileId, long permissions) {
        GroupMember obj = new GroupMember();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.profileId = profileId;
        obj.role = GroupMemberRole.ADMIN;
        obj.status = GroupMemberStatus.ACTIVE;
        obj.permissions = permissions;
        return obj;
    }

    /**
     * Creates a regular member with no permissions.
     */
    public static GroupMember createMember(Long groupId, Long profileId) {
        GroupMember obj = new GroupMember();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.profileId = profileId;
        obj.role = GroupMemberRole.MEMBER;
        obj.status = GroupMemberStatus.ACTIVE;
        obj.permissions = GroupAdminPermission.NONE;
        return obj;
    }

    public static GroupMember reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, Long profileId, GroupMemberRole role, GroupMemberStatus status,
            Instant mutedUntil, Long permissions
    ) {
        GroupMember obj = new GroupMember();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.profileId = profileId;
        obj.role = role;
        obj.status = status;
        obj.mutedUntil = mutedUntil;
        obj.permissions = permissions != null ? permissions : GroupAdminPermission.NONE;
        return obj;
    }

    // -----------------------------------------------------------------------
    // Permission checks
    // -----------------------------------------------------------------------

    /**
     * Returns {@code true} if this member holds the given permission.
     * OWNER always returns {@code true}; MEMBER always returns {@code false}.
     */
    public boolean hasPermission(GroupAdminPermission permission) {
        if (role == GroupMemberRole.OWNER) return true;
        if (role != GroupMemberRole.ADMIN) return false;
        return (permissions & permission.getBit()) != 0;
    }

    /**
     * Returns {@code true} if this member holds every permission in the given bitmask.
     */
    public boolean hasAllPermissions(long permissionMask) {
        if (role == GroupMemberRole.OWNER) return true;
        if (role != GroupMemberRole.ADMIN) return false;
        return (permissions & permissionMask) == permissionMask;
    }

    // -----------------------------------------------------------------------
    // Permission mutations (for ADMIN role only)
    // -----------------------------------------------------------------------

    /**
     * Grants the given permission to this admin member.
     * Has no effect if the member is not ADMIN.
     */
    public void grantPermission(GroupAdminPermission permission) {
        if (role != GroupMemberRole.ADMIN) return;
        permissions |= permission.getBit();
    }

    /**
     * Revokes the given permission from this admin member.
     * Has no effect if the member is not ADMIN.
     */
    public void revokePermission(GroupAdminPermission permission) {
        if (role != GroupMemberRole.ADMIN) return;
        permissions &= ~permission.getBit();
    }

    /**
     * Replaces the entire permission bitmask for this admin member.
     * Has no effect if the member is not ADMIN.
     */
    public void setPermissions(long permissionMask) {
        if (role != GroupMemberRole.ADMIN) return;
        permissions = permissionMask;
    }
}
