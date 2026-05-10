package dev.ngb.app.attachment.web;

import dev.ngb.app.attachment.application.usecase.complete_attachment.CompleteAttachmentUseCase;
import dev.ngb.app.attachment.application.usecase.presign_attachment.PresignAttachmentUseCase;
import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentRequest;
import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentResponse;
import dev.ngb.infrastructure.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AttachmentResource implements AttachmentApi {

    private final PresignAttachmentUseCase presignAttachmentUseCase;
    private final CompleteAttachmentUseCase completeAttachmentUseCase;

    @Override
    @Transactional
    public ResponseEntity<PresignAttachmentResponse> presign(PresignAttachmentRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        PresignAttachmentResponse body = presignAttachmentUseCase.execute(accountId, request);
        return ResponseEntity.ok(body);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> complete(String attachmentUuid) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        completeAttachmentUseCase.execute(accountId, attachmentUuid);
        return ResponseEntity.noContent().build();
    }
}
