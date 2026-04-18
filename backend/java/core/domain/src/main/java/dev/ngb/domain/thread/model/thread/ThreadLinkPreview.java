package dev.ngb.domain.thread.model.thread;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Open Graph preview metadata for a URL embedded in a thread.
 */
@Getter
public class ThreadLinkPreview extends DomainEntity<Long> {

    private ThreadLinkPreview() {}

    private Long threadId;
    private String url;
    private String title;
    private String description;
    private String imageUrl;

    public static ThreadLinkPreview reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, String url, String title, String description, String imageUrl) {
        ThreadLinkPreview obj = new ThreadLinkPreview();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.url = url;
        obj.title = title;
        obj.description = description;
        obj.imageUrl = imageUrl;
        return obj;
    }
}
