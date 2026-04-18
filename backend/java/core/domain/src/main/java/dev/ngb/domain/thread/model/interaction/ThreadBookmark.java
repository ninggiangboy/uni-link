package dev.ngb.domain.thread.model.interaction;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile bookmarking a thread for later reference.
 */
@Getter
public class ThreadBookmark extends DomainEntity<Long> {

    private ThreadBookmark() {}

    private Long threadId;
    private Long profileId;

    public static ThreadBookmark reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Long profileId) {
        ThreadBookmark obj = new ThreadBookmark();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.profileId = profileId;
        return obj;
    }
}
