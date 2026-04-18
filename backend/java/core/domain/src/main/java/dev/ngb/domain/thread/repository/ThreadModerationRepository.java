package dev.ngb.domain.thread.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.thread.model.moderation.ThreadModeration;

/**
 * Repository for managing {@link ThreadModeration} reports.
 */
public interface ThreadModerationRepository extends Repository<ThreadModeration, Long> {
}
