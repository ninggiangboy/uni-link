package dev.ngb.app.attachment.domain;

import dev.ngb.domain.DomainError;
import dev.ngb.domain.DomainErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentError implements DomainError {

    INVALID_FILE_NAME("File name is required and must be a single path segment", DomainErrorType.VALIDATION),
    CONTENT_TYPE_NOT_ALLOWED("Content type is not allowed for this attachment", DomainErrorType.VALIDATION),
    FILE_TOO_LARGE("Declared file size exceeds the maximum allowed", DomainErrorType.VALIDATION),
    SIZE_DECLARATION_REQUIRED(
            "Declared size is required when a maximum attachment size is configured",
            DomainErrorType.VALIDATION
    ),
    CONTENT_TYPE_REQUIRED("Content type is required", DomainErrorType.VALIDATION),
    ATTACHMENT_NOT_FOUND("Attachment not found", DomainErrorType.NOT_FOUND),
    ATTACHMENT_NOT_PENDING(
            "Attachment cannot be completed in its current state",
            DomainErrorType.CONFLICT
    ),
    ATTACHMENT_OBJECT_NOT_IN_STORAGE(
            "Object was not found in storage for this attachment",
            DomainErrorType.VALIDATION
    );

    private final String message;
    private final DomainErrorType type;
}
