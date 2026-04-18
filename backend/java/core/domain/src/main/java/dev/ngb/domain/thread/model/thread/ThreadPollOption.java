package dev.ngb.domain.thread.model.thread;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A single option within a thread poll.
 */
@Getter
public class ThreadPollOption extends DomainEntity<Long> {

    private ThreadPollOption() {}

    private Long threadId;
    private String option;
    private Integer position;
    private Long voteCount;

    public static ThreadPollOption reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, String option, Integer position, Long voteCount) {
        ThreadPollOption obj = new ThreadPollOption();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.option = option;
        obj.position = position;
        obj.voteCount = voteCount;
        return obj;
    }
}
