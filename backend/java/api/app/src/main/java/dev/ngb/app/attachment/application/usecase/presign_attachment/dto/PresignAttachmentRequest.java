package dev.ngb.app.attachment.application.usecase.presign_attachment.dto;

import dev.ngb.constant.AttachmentConstants;
import dev.ngb.domain.attachment.model.attachment.AttachmentType;
import dev.ngb.util.ContentTypeUtils;
import dev.ngb.util.FilenameUtils;
import dev.ngb.util.validation.FluentValidator;

public record PresignAttachmentRequest(
        String fileName,
        String contentType,
        Long sizeBytes,
        AttachmentType type
) {

    public PresignAttachmentRequest {
        String normalizedFileName = FilenameUtils.trimToEmpty(fileName);
        String normalizedContentType = contentType == null ? "" : contentType.trim();
        AttachmentType normalizedType = type == null ? AttachmentType.ATTACHMENT : type;
        fileName = normalizedFileName;
        contentType = normalizedContentType;
        type = normalizedType;

        FluentValidator.of(this)
                .ruleFor("fileName", ignored -> normalizedFileName)
                .notNullOrBlank()
                .must(value -> FilenameUtils.resolveForAttachmentUpload(
                        value,
                        FilenameUtils.DEFAULT_MAX_STORAGE_BASENAME_LENGTH
                ).isPresent(), "File name is invalid")
                .ruleFor("contentType", ignored -> normalizedContentType)
                .notEmpty()
                .must(value -> ContentTypeUtils.isAllowedContentType(value, AttachmentConstants.ALLOWED_CONTENT_TYPES), "Content type is not allowed")
                .ruleFor("sizeBytes", ignored -> sizeBytes)
                .notNull()
                .greaterThan(0)
                .lessOrEqual(AttachmentConstants.MAX_ATTACHMENT_SIZE_BYTES)
                .ruleFor("type", ignored -> normalizedType)
                .notNull()
                .validateAndThrow();
    }
}
