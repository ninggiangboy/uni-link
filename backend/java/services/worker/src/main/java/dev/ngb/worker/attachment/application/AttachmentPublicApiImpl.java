package dev.ngb.worker.attachment.application;

import dev.ngb.worker.attachment.application.usecase.sweep.SweepStalePendingAttachmentsUseCase;
import dev.ngb.worker.shared.public_api.AttachmentPublicApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttachmentPublicApiImpl implements AttachmentPublicApi {

    private final SweepStalePendingAttachmentsUseCase sweepStalePendingAttachmentsUseCase;

    @Override
    public void executeSweepStalePendingAttachments() {
        sweepStalePendingAttachmentsUseCase.execute();
    }
}
