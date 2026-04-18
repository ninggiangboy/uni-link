package dev.ngb.domain.hashtag.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.hashtag.model.analytics.HashtagUsageHourly;

/**
 * Repository for managing {@link HashtagUsageHourly} analytical buckets.
 */
public interface HashtagUsageHourlyRepository extends Repository<HashtagUsageHourly, Long> {
}
