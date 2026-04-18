package dev.ngb.domain.hashtag.model.hashtag;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Moderation state for a hashtag, allowing restriction or banning.
 */
@Getter
public class HashtagModeration extends DomainEntity<Long> {

    private HashtagModeration() {}

    private Long hashtagId;
    private HashtagModerationStatus status;
    private String reason;

    public static HashtagModeration reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long hashtagId, HashtagModerationStatus status, String reason) {
        HashtagModeration obj = new HashtagModeration();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.hashtagId = hashtagId;
        obj.status = status;
        obj.reason = reason;
        return obj;
    }
}
