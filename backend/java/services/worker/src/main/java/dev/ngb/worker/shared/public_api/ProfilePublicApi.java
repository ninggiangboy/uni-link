package dev.ngb.worker.shared.public_api;

import dev.ngb.application.PublicApi;

import java.util.List;

public interface ProfilePublicApi extends PublicApi {
    void executeApplyProfileFollowStatsDeltaBatchUseCase(List<String> payloads);
}
