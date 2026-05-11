package dev.ngb.worker.profile.application.usecase.flush_follow_deltas;

import dev.ngb.application.UseCaseService;
import dev.ngb.application.port.follow.FollowDeltaFlushPort;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FlushFollowDeltasUseCase implements UseCaseService {

    private final FollowDeltaFlushPort followDeltaFlushPort;
    private final ProfileStatsRepository profileStatsRepository;

    public void execute() {
        var changes = followDeltaFlushPort.getAllAndReset();
        if (changes.isEmpty()) {
            log.trace("No pending follow deltas to flush");
            return;
        }
        profileStatsRepository.adjustCountsBulk(changes);
        log.info("Flushed {} follow deltas to DB", changes.size());
    }
}
