package dev.ngb.app.attachment.web;

import dev.ngb.app.attachment.application.usecase.complete_attachment.CompleteAttachmentUseCase;
import dev.ngb.app.attachment.application.usecase.presign_attachment.PresignAttachmentUseCase;
import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentRequest;
import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AttachmentResource implements AttachmentApi {

    private final PresignAttachmentUseCase presignAttachmentUseCase;
    private final CompleteAttachmentUseCase completeAttachmentUseCase;

    @Override
    @Transactional
    public ResponseEntity<PresignAttachmentResponse> presign(PresignAttachmentRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        PresignAttachmentResponse body = presignAttachmentUseCase.execute(accountId, request);
        return ResponseEntity.ok(body);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> complete(String attachmentUuid, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        completeAttachmentUseCase.execute(accountId, attachmentUuid);
        return ResponseEntity.noContent().build();
    }
}
