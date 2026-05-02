package dev.ngb.app.attachment.application;

import dev.ngb.app.shared.public_api.AttachmentPublicApi;
import dev.ngb.application.PublicApi;
import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AttachmentPublicApiImpl implements AttachmentPublicApi, PublicApi {

    private final AttachmentRepository attachmentRepository;

    @Override
    public Optional<String> resolveAvailableUrl(String attachmentUuid, Long accountId) {
        if (attachmentUuid == null || accountId == null) {
            return Optional.empty();
        }
        return attachmentRepository.findByUuidAndAccountId(attachmentUuid, accountId)
                .filter(att -> att.getUploadStatus() == AttachmentUploadStatus.AVAILABLE)
                .map(Attachment::getFileUrl);
    }
}
