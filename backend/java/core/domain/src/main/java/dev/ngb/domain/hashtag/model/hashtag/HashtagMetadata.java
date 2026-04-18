package dev.ngb.domain.hashtag.model.hashtag;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Discovery metadata for a hashtag (category, language, description) used in recommendations.
 */
@Getter
public class HashtagMetadata extends DomainEntity<Long> {

    private HashtagMetadata() {}

    private Long hashtagId;
    private String category;
    private String language;
    private String description;

    public static HashtagMetadata reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long hashtagId, String category, String language, String description) {
        HashtagMetadata obj = new HashtagMetadata();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.hashtagId = hashtagId;
        obj.category = category;
        obj.language = language;
        obj.description = description;
        return obj;
    }
}
