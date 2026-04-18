package dev.ngb.domain.attachment.model.attachment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public enum AttachmentType {
    ATTACHMENT("public", "attachments"),
    AVATAR("public", "avatars"),;

    private final String bucket;
    private final String prefix;

    public String getObjectKey(String fileName, Long accountId) {
        return String.format("%s/%s/%s-%s", prefix, accountId, UUID.randomUUID(), fileName);
    }
}
