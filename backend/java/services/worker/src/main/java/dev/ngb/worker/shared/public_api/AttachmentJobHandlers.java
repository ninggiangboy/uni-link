package dev.ngb.worker.shared.public_api;

import dev.ngb.application.PublicApi;

public interface AttachmentJobHandlers extends PublicApi {

    void executeSweepStalePendingAttachmentsUseCase();

}
