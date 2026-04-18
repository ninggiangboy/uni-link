package dev.ngb.domain.group.model.content;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A rule or guideline defined by group admins that members are expected to follow.
 */
@Getter
public class GroupRule extends DomainEntity<Long> {

    private GroupRule() {}

    private Long groupId;
    private String title;
    private String description;
    private Integer position;

    public static GroupRule create(Long groupId, String title, String description, Integer position) {
        GroupRule obj = new GroupRule();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.title = title;
        obj.description = description;
        obj.position = position != null ? position : 0;
        return obj;
    }

    public static GroupRule reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, String title, String description, Integer position
    ) {
        GroupRule obj = new GroupRule();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.title = title;
        obj.description = description;
        obj.position = position;
        return obj;
    }
}
