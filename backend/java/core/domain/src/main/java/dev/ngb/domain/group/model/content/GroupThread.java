package dev.ngb.domain.group.model.content;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Associates a thread with a group. Owned by the group bounded context to avoid
 * coupling the thread domain to groups.
 */
@Getter
public class GroupThread extends DomainEntity<Long> {

    private GroupThread() {}

    private Long groupId;
    private Long threadId;
    private Boolean isPinned;

    public static GroupThread create(Long groupId, Long threadId) {
        GroupThread obj = new GroupThread();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.threadId = threadId;
        obj.isPinned = Boolean.FALSE;
        return obj;
    }

    public static GroupThread reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, Long threadId, Boolean isPinned
    ) {
        GroupThread obj = new GroupThread();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.threadId = threadId;
        obj.isPinned = isPinned;
        return obj;
    }
}
