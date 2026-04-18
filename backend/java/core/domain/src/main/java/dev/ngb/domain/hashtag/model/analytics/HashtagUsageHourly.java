package dev.ngb.domain.hashtag.model.analytics;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Hourly time-bucketed usage count for trending calculation and analytics.
 */
@Getter
public class HashtagUsageHourly extends DomainEntity<Long> {

    private HashtagUsageHourly() {}

    private Long hashtagId;
    private Instant hourBucket;
    private Long count;

    public static HashtagUsageHourly reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long hashtagId, Instant hourBucket, Long count) {
        HashtagUsageHourly obj = new HashtagUsageHourly();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.hashtagId = hashtagId;
        obj.hourBucket = hourBucket;
        obj.count = count;
        return obj;
    }
}
