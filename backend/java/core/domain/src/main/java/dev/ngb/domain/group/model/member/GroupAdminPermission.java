package dev.ngb.domain.group.model.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

/**
 * Fine-grained permissions that can be granted to an ADMIN member of a group.
 * <p>
 * Each permission occupies a single bit of a {@code long} bitmask stored in
 * {@code GroupMember.permissions}. OWNER members implicitly hold every permission
 * regardless of the stored value. MEMBER role holders are never granted permissions.
 * <p>
 * Bit layout (LSB = bit 0):
 * <pre>
 *  Bit  Permission
 *   0   APPROVE_POSTS
 *   1   REMOVE_POSTS
 *   2   PIN_POSTS
 *   3   HANDLE_REPORTS
 *   4   APPROVE_MEMBERS
 *   5   REMOVE_MEMBERS
 *   6   BAN_MEMBERS
 *   7   MUTE_MEMBERS
 *   8   MANAGE_INVITATIONS
 *   9   MANAGE_RULES
 *  10   MANAGE_GROUP_INFO
 *  11   MANAGE_ADMIN_PERMISSIONS
 * </pre>
 * Up to 63 permissions can be added in the future (bits 0–62 of a signed long).
 */
@Getter
@RequiredArgsConstructor
public enum GroupAdminPermission {

    /** Approve pending posts before they are visible in the group feed. */
    APPROVE_POSTS(1L),

    /** Remove (delete) any post in the group. */
    REMOVE_POSTS(1L << 1),

    /** Pin or unpin posts at the top of the group feed. */
    PIN_POSTS(1L << 2),

    /** Review and resolve member-submitted reports on posts or comments. */
    HANDLE_REPORTS(1L << 3),

    /** Approve or reject join requests from new members. */
    APPROVE_MEMBERS(1L << 4),

    /** Remove existing members from the group. */
    REMOVE_MEMBERS(1L << 5),

    /** Permanently ban profiles from the group. */
    BAN_MEMBERS(1L << 6),

    /** Temporarily mute members so they cannot post. */
    MUTE_MEMBERS(1L << 7),

    /** Send, revoke, and manage group invitations. */
    MANAGE_INVITATIONS(1L << 8),

    /** Create, edit, and delete group rules. */
    MANAGE_RULES(1L << 9),

    /** Edit the group name, description, avatar, banner, and settings. */
    MANAGE_GROUP_INFO(1L << 10),

    /** Grant or revoke admin permissions for other admin members. */
    MANAGE_ADMIN_PERMISSIONS(1L << 11);

    private final long bit;

    /** Bitmask with every defined permission enabled. */
    public static final long ALL = computeAll();

    /** Bitmask with no permissions (regular member). */
    public static final long NONE = 0L;

    // -----------------------------------------------------------------------
    // Utility helpers
    // -----------------------------------------------------------------------

    /**
     * Returns the set of permissions encoded in the given bitmask.
     */
    public static Set<GroupAdminPermission> fromBitmask(long bitmask) {
        Set<GroupAdminPermission> result = EnumSet.noneOf(GroupAdminPermission.class);
        for (GroupAdminPermission p : values()) {
            if ((bitmask & p.bit) != 0) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Returns a bitmask that represents exactly the given set of permissions.
     */
    public static long toBitmask(Set<GroupAdminPermission> permissions) {
        long mask = NONE;
        for (GroupAdminPermission p : permissions) {
            mask |= p.bit;
        }
        return mask;
    }

    private static long computeAll() {
        long mask = NONE;
        for (GroupAdminPermission p : values()) {
            mask |= p.bit;
        }
        return mask;
    }
}
