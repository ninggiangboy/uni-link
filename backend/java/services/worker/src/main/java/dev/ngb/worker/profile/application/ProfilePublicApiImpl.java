package dev.ngb.worker.profile.application;

import dev.ngb.worker.profile.application.usecase.flush_follow_deltas.FlushFollowDeltasUseCase;
import dev.ngb.worker.shared.public_api.ProfilePublicApi;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfilePublicApiImpl implements ProfilePublicApi {

    private final FlushFollowDeltasUseCase flushFollowDeltasUseCase;

    @Override
    public void flushFollowDeltas() {
        flushFollowDeltasUseCase.execute();
    }
}
