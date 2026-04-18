package dev.ngb.domain.group.model.group;

import dev.ngb.domain.DomainEntity;
import dev.ngb.util.StringUtils;
import lombok.Getter;

import java.time.Instant;

/**
 * A community space where profiles can post threads, interact, and be governed
 * by owners and moderators.
 * <p>
 * Aggregate root of the group bounded context. Members, rules, stats, invitations,
 * join requests, threads, and moderation actions are separate entities with their
 * own repositories.
 */
@Getter
public class Group extends DomainEntity<Long> {

    private Group() {}

    private Long ownerProfileId;

    private String name;
    private String slug;
    private String description;

    private String avatarUrl;
    private String bannerUrl;

    private GroupVisibility visibility;
    private GroupStatus status;
    private Boolean requiresApproval;

    public static Group create(
            Long ownerProfileId,
            String name,
            String slug,
            String description,
            GroupVisibility visibility
    ) {
        Group obj = new Group();
        obj.createdAt = Instant.now(obj.clock);
        obj.ownerProfileId = ownerProfileId;
        obj.name = StringUtils.trim(name);
        obj.slug = StringUtils.trim(slug);
        obj.description = StringUtils.trim(description);
        obj.visibility = visibility != null ? visibility : GroupVisibility.PUBLIC;
        obj.status = GroupStatus.ACTIVE;
        obj.requiresApproval = visibility == GroupVisibility.PRIVATE || visibility == GroupVisibility.SECRET;
        return obj;
    }

    public static Group reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long ownerProfileId, String name, String slug, String description,
            String avatarUrl, String bannerUrl,
            GroupVisibility visibility, GroupStatus status, Boolean requiresApproval
    ) {
        Group obj = new Group();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.ownerProfileId = ownerProfileId;
        obj.name = name;
        obj.slug = slug;
        obj.description = description;
        obj.avatarUrl = avatarUrl;
        obj.bannerUrl = bannerUrl;
        obj.visibility = visibility;
        obj.status = status;
        obj.requiresApproval = requiresApproval;
        return obj;
    }
}
