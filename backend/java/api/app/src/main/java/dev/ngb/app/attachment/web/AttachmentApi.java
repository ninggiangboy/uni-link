package dev.ngb.app.attachment.web;

import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentRequest;
import dev.ngb.app.attachment.application.usecase.presign_attachment.dto.PresignAttachmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Attachments", description = "Account-owned file attachments")
@RequestMapping("/api/attachments")
public interface AttachmentApi {

    @Operation(summary = "Create presigned PUT URL and persist attachment metadata")
    @PostMapping("/presign")
    ResponseEntity<PresignAttachmentResponse> presign(
            @RequestBody PresignAttachmentRequest request,
            @AuthenticationPrincipal Jwt jwt
    );

    @Operation(summary = "Confirm upload finished and mark attachment available when the object exists in storage")
    @PostMapping("/{attachmentUuid}/complete")
    ResponseEntity<Void> complete(
            @PathVariable String attachmentUuid,
            @AuthenticationPrincipal Jwt jwt
    );
}
