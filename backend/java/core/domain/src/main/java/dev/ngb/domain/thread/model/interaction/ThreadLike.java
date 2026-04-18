package dev.ngb.domain.thread.model.interaction;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile liking a thread.
 */
@Getter
public class ThreadLike extends DomainEntity<Long> {

    private ThreadLike() {}

    private Long threadId;
    private Long profileId;

    public static ThreadLike reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Long profileId) {
        ThreadLike obj = new ThreadLike();
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
