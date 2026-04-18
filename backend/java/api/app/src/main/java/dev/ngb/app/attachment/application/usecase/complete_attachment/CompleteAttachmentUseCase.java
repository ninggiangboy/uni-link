package dev.ngb.app.attachment.application.usecase.complete_attachment;

import dev.ngb.app.attachment.domain.AttachmentError;
import dev.ngb.application.UseCaseService;
import dev.ngb.application.port.storage.ObjectStorage;
import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompleteAttachmentUseCase implements UseCaseService {

    private final AttachmentRepository attachmentRepository;
    private final ObjectStorage objectStorage;

    public void execute(Long accountId, String attachmentUuid) {
        Attachment attachment = attachmentRepository
                .findByUuidAndAccountId(attachmentUuid, accountId)
                .orElseThrow(AttachmentError.ATTACHMENT_NOT_FOUND::exception);
        if (attachment.getUploadStatus() != AttachmentUploadStatus.PENDING_PUT) {
            throw AttachmentError.ATTACHMENT_NOT_PENDING.exception();
        }
        if (!objectStorage.objectExists(attachment.getType().getBucket(), attachment.getObjectKey())) {
            throw AttachmentError.ATTACHMENT_OBJECT_NOT_IN_STORAGE.exception();
        }
        attachment.markAvailable();
        attachmentRepository.save(attachment);
    }
}
