package dev.ngb.domain.chat.model.message;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Links a message to a media asset managed by the media service.
 */
@Getter
public class MessageMedia extends DomainEntity<Long> {

    private MessageMedia() {}

    private Long messageId;
    private Long mediaId;

    public static MessageMedia reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long messageId, Long mediaId) {
        MessageMedia obj = new MessageMedia();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.messageId = messageId;
        obj.mediaId = mediaId;
        return obj;
    }
}
