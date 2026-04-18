package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.graph.ProfileFollow;

/**
 * Repository for managing {@link ProfileFollow} follow relationships.
 */
public interface ProfileFollowRepository extends Repository<ProfileFollow, Long> {
}
