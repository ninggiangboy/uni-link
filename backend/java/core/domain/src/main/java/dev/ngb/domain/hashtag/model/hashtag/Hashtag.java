package dev.ngb.domain.hashtag.model.hashtag;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A hashtag used for content indexing and discovery, with normalized form for search.
 * <p>
 * Aggregate root of the hashtag domain.
 * Relations (co-occurrence graph) and hourly usage buckets are separate analytical entities.
 */
@Getter
public class Hashtag extends DomainEntity<Long> {

    private Hashtag() {}

    private String tag;
    private String normalizedTag;
    private Long usageCount;
    private Long threadCount;

    public static Hashtag reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            String tag, String normalizedTag, Long usageCount, Long threadCount) {
        Hashtag obj = new Hashtag();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.tag = tag;
        obj.normalizedTag = normalizedTag;
        obj.usageCount = usageCount;
        obj.threadCount = threadCount;
        return obj;
    }
}
