package dev.ngb.worker.attachment.application;

import dev.ngb.worker.attachment.application.sweep.SweepStalePendingAttachmentsUseCase;
import dev.ngb.worker.shared.public_api.AttachmentJobHandlers;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttachmentJobHandlersImpl implements AttachmentJobHandlers {

    private final SweepStalePendingAttachmentsUseCase sweepStalePendingAttachmentsUseCase;

    @Override
    public void executeSweepStalePendingAttachmentsUseCase() {
        sweepStalePendingAttachmentsUseCase.execute();
    }
}
