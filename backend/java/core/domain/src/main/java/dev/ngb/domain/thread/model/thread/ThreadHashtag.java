package dev.ngb.domain.thread.model.thread;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Maps a thread to a hashtag, with position tracking within the thread content.
 */
@Getter
public class ThreadHashtag extends DomainEntity<Long> {

    private ThreadHashtag() {}

    private Long threadId;
    private Long hashtagId;
    private Integer positionStart;
    private Integer positionEnd;

    public static ThreadHashtag reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Long hashtagId, Integer positionStart, Integer positionEnd) {
        ThreadHashtag obj = new ThreadHashtag();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.hashtagId = hashtagId;
        obj.positionStart = positionStart;
        obj.positionEnd = positionEnd;
        return obj;
    }
}
