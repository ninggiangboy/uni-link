package dev.ngb.application.port.follow;

import dev.ngb.domain.profile.model.stats.FollowCountChange;

import java.util.List;

public interface FollowDeltaFlushPort {

    List<FollowCountChange> getAllAndReset();
}
