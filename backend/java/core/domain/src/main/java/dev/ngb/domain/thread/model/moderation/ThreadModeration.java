package dev.ngb.domain.thread.model.moderation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Report filed against a thread, with moderation workflow tracking.
 */
@Getter
public class ThreadModeration extends DomainEntity<Long> {

    private ThreadModeration() {}

    private Long threadId;
    private Long reporterProfileId;
    private String reason;

    private ThreadModerationStatus status;
    private Long moderatorProfileId;
    private String resolution;

    public static ThreadModeration reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long threadId, Long reporterProfileId, String reason,
            ThreadModerationStatus status, Long moderatorProfileId, String resolution) {
        ThreadModeration obj = new ThreadModeration();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.threadId = threadId;
        obj.reporterProfileId = reporterProfileId;
        obj.reason = reason;
        obj.status = status;
        obj.moderatorProfileId = moderatorProfileId;
        obj.resolution = resolution;
        return obj;
    }
}
