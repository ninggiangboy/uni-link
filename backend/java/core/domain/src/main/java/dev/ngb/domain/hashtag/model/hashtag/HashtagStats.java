package dev.ngb.domain.hashtag.model.hashtag;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Denormalized usage counters for a hashtag, updated asynchronously.
 */
@Getter
public class HashtagStats extends DomainEntity<Long> {

    private HashtagStats() {}

    private Long hashtagId;
    private Long threadCount;
    private Long usageCount;
    private Instant lastUsedAt;

    public static HashtagStats reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long hashtagId, Long threadCount, Long usageCount, Instant lastUsedAt) {
        HashtagStats obj = new HashtagStats();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.hashtagId = hashtagId;
        obj.threadCount = threadCount;
        obj.usageCount = usageCount;
        obj.lastUsedAt = lastUsedAt;
        return obj;
    }
}
