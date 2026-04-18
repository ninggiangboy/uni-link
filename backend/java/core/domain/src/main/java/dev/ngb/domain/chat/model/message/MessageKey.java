package dev.ngb.domain.chat.model.message;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Per-recipient encrypted symmetric key for a group message.
 * <p>
 * For direct (1:1) messages, no key entry is needed — the shared secret is derived
 * via ECDH from the two parties' key pairs. For group messages, each recipient gets
 * a separate row containing the message's symmetric key encrypted with their public key.
 */
@Getter
public class MessageKey extends DomainEntity<Long> {

    private MessageKey() {}

    private Long messageId;
    private Long profileId;
    private String encryptedKey;

    public static MessageKey reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long messageId, Long profileId, String encryptedKey) {
        MessageKey obj = new MessageKey();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.messageId = messageId;
        obj.profileId = profileId;
        obj.encryptedKey = encryptedKey;
        return obj;
    }
}
