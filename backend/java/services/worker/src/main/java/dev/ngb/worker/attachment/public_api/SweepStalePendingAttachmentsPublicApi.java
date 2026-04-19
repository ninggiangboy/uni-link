package dev.ngb.worker.attachment.public_api;

import dev.ngb.application.PublicApi;

public interface SweepStalePendingAttachmentsPublicApi extends PublicApi {

    void execute();
}
