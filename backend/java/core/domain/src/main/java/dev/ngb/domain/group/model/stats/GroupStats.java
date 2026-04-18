package dev.ngb.domain.group.model.stats;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Denormalized counters for a group, updated asynchronously.
 */
@Getter
public class GroupStats extends DomainEntity<Long> {

    private GroupStats() {}

    private Long groupId;
    private Long memberCount;
    private Long threadCount;

    public static GroupStats createForNewGroup(Long groupId) {
        GroupStats obj = new GroupStats();
        obj.createdAt = Instant.now(obj.clock);
        obj.groupId = groupId;
        obj.memberCount = 0L;
        obj.threadCount = 0L;
        return obj;
    }

    public static GroupStats reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long groupId, Long memberCount, Long threadCount
    ) {
        GroupStats obj = new GroupStats();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.groupId = groupId;
        obj.memberCount = memberCount;
        obj.threadCount = threadCount;
        return obj;
    }
}
