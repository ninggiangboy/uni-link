package dev.ngb.domain.hashtag.model.analytics;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Co-occurrence relationship between two hashtags, tracking how often they appear together.
 */
@Getter
public class HashtagRelation extends DomainEntity<Long> {

    private HashtagRelation() {}

    private Long hashtagAId;
    private Long hashtagBId;
    private Long coOccurrenceCount;

    public static HashtagRelation reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long hashtagAId, Long hashtagBId, Long coOccurrenceCount) {
        HashtagRelation obj = new HashtagRelation();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.hashtagAId = hashtagAId;
        obj.hashtagBId = hashtagBId;
        obj.coOccurrenceCount = coOccurrenceCount;
        return obj;
    }
}
