package dev.ngb.app.attachment.application.usecase.presign_attachment.dto;

import java.time.Instant;
import java.util.Map;

public record PresignAttachmentResponse(
        String attachmentUuid,
        String uploadUrl,
        Map<String, String> uploadHeaders,
        String objectKey,
        String fileUrl,
        Instant expiresAt
) {
}
