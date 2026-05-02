package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.profile.Profile;

import java.util.Optional;

/**
 * Repository for managing {@link Profile} aggregates.
 */
public interface ProfileRepository extends Repository<Profile, Long> {

    boolean existsByUsername(String username);

    boolean existsByAccountId(Long accountId);

    Optional<Profile> findByUsername(String username);

    Optional<Profile> findByAccountId(Long accountId);
}
