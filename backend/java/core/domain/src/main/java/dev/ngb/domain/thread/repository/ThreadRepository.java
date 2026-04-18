package dev.ngb.domain.thread.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.thread.model.thread.Thread;

/**
 * Repository for managing {@link Thread} aggregates.
 */
public interface ThreadRepository extends Repository<Thread, Long> {
}
