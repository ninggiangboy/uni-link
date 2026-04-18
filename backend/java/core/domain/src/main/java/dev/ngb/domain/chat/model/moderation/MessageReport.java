package dev.ngb.domain.chat.model.moderation;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Report filed against a message for moderation.
 */
@Getter
public class MessageReport extends DomainEntity<Long> {

    private MessageReport() {}

    private Long messageId;
    private Long reporterProfileId;
    private String reason;

    public static MessageReport reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long messageId, Long reporterProfileId, String reason) {
        MessageReport obj = new MessageReport();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.messageId = messageId;
        obj.reporterProfileId = reporterProfileId;
        obj.reason = reason;
        return obj;
    }
}
