package dev.ngb.app.attachment.application.usecase.presign_attachment;

import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentRequest;
import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentResponse;
import dev.ngb.app.attachment.domain.AttachmentError;
import dev.ngb.application.UseCaseService;
import dev.ngb.application.port.storage.ObjectStorage;
import dev.ngb.constant.AttachmentConstants;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.model.attachment.AttachmentType;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import dev.ngb.util.FilenameUtils;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class PresignAttachmentUseCase implements UseCaseService {

    private final AttachmentRepository attachmentRepository;
    private final ObjectStorage objectStorage;

    public PresignAttachmentResponse execute(Long accountId, PresignAttachmentRequest request) {
        AttachmentType type = request.type();

        FilenameUtils.ResolvedUploadFilename resolvedFileName = FilenameUtils
                .resolveForAttachmentUpload(request.fileName(), FilenameUtils.DEFAULT_MAX_STORAGE_BASENAME_LENGTH)
                .orElseThrow(AttachmentError.INVALID_FILE_NAME::exception);

        String objectKey = type.getObjectKey(resolvedFileName.storageBasename(), accountId);

        Duration ttl = Duration.ofSeconds(Math.max(
                AttachmentConstants.MIN_PRESIGN_TTL_SECONDS,
                AttachmentConstants.PRESIGN_TTL_SECONDS
        ));

        ObjectStorage.PresignedPutResult signed = objectStorage.presignPut(
                type.getBucket(), objectKey, request.contentType(), ttl
        );

        Attachment pending = Attachment.createPendingPut(
                accountId,
                type,
                objectKey,
                resolvedFileName.displayName(),
                request.contentType(),
                request.sizeBytes(),
                signed.fileUrl(),
                accountId
        );
        Attachment saved = attachmentRepository.save(pending);

        return new PresignAttachmentResponse(
                saved.getUuid(),
                signed.uploadUrl(),
                signed.uploadHeaders(),
                objectKey,
                signed.fileUrl(),
                signed.expiresAt()
        );
    }
}
