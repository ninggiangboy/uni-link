package dev.ngb.domain.thread.model.thread;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Poll attached to a thread, holding poll-level settings like expiration and multi-vote.
 */
@Getter
public class ThreadPoll extends DomainEntity<Long> {

    private ThreadPoll() {}

    private Long threadId;
    private Instant expiresAt;
    private Boolean allowMultipleVotes;

    public static ThreadPoll reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Instant expiresAt, Boolean allowMultipleVotes) {
        ThreadPoll obj = new ThreadPoll();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.expiresAt = expiresAt;
        obj.allowMultipleVotes = allowMultipleVotes;
        return obj;
    }
}
