package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.username.ProfileUsername;

import java.util.List;

/**
 * Repository for managing {@link ProfileUsername} entities (separate from Profile aggregate).
 */
public interface ProfileUsernameRepository extends Repository<ProfileUsername, Long> {

    List<ProfileUsername> findByProfileId(Long profileId);
}
