package dev.ngb.domain.thread.model.interaction;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile reposting (sharing) a thread.
 */
@Getter
public class ThreadRepost extends DomainEntity<Long> {

    private ThreadRepost() {}

    private Long threadId;
    private Long profileId;

    public static ThreadRepost reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Long profileId) {
        ThreadRepost obj = new ThreadRepost();
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
