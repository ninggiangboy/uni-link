package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.content.GroupThread;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link GroupThread} associations between groups and threads.
 */
public interface GroupThreadRepository extends Repository<GroupThread, Long> {

    List<GroupThread> findByGroupId(Long groupId);

    Optional<GroupThread> findByGroupIdAndThreadId(Long groupId, Long threadId);

    boolean existsByGroupIdAndThreadId(Long groupId, Long threadId);
}
