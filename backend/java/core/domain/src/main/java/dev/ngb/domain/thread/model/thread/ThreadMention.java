package dev.ngb.domain.thread.model.thread;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a profile being mentioned in a thread.
 */
@Getter
public class ThreadMention extends DomainEntity<Long> {

    private ThreadMention() {}

    private Long threadId;
    private Long mentionedProfileId;

    public static ThreadMention reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Long mentionedProfileId) {
        ThreadMention obj = new ThreadMention();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.mentionedProfileId = mentionedProfileId;
        return obj;
    }
}
