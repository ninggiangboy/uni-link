package dev.ngb.domain.thread.model.thread;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Media attachment on a thread (image, video, GIF) with dimensions and accessibility text.
 */
@Getter
public class ThreadMedia extends DomainEntity<Long> {

    private ThreadMedia() {}

    private Long threadId;

    private MediaType type;

    private String url;
    private String thumbnailUrl;

    private Integer width;
    private Integer height;
    private Integer duration;

    private String altText;
    private Integer position;

    public static ThreadMedia reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, MediaType type, String url, String thumbnailUrl,
            Integer width, Integer height, Integer duration, String altText, Integer position) {
        ThreadMedia obj = new ThreadMedia();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.type = type;
        obj.url = url;
        obj.thumbnailUrl = thumbnailUrl;
        obj.width = width;
        obj.height = height;
        obj.duration = duration;
        obj.altText = altText;
        obj.position = position;
        return obj;
    }
}
