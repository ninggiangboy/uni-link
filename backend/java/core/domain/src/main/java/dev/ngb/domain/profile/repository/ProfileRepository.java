package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.profile.Profile;

/**
 * Repository for managing {@link Profile} aggregates.
 */
public interface ProfileRepository extends Repository<Profile, Long> {

    boolean existsByAccountId(Long accountId);

    boolean existsByUsername(String username);
}
