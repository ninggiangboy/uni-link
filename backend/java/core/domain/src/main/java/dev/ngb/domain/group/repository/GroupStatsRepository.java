package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.stats.GroupStats;

import java.util.Optional;

/**
 * Repository for managing {@link GroupStats} entities.
 */
public interface GroupStatsRepository extends Repository<GroupStats, Long> {

    Optional<GroupStats> findByGroupId(Long groupId);
}
