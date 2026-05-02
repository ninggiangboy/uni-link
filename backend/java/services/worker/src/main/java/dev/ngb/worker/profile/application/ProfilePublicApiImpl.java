package dev.ngb.worker.profile.application;

import dev.ngb.worker.profile.application.usecase.stats_delta.ApplyProfileFollowStatsDeltaBatchUseCase;
import dev.ngb.worker.shared.public_api.ProfilePublicApi;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ProfilePublicApiImpl implements ProfilePublicApi {

    private final ApplyProfileFollowStatsDeltaBatchUseCase applyProfileFollowStatsDeltaBatchUseCase;

    @Override
    public void executeApplyProfileFollowStatsDeltaBatchUseCase(List<String> payloads) {
        applyProfileFollowStatsDeltaBatchUseCase.execute(payloads);
    }

}
