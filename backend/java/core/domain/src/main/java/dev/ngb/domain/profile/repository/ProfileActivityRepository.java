package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.activity.ProfileActivity;

import java.util.Optional;

/**
 * Repository for managing {@link ProfileActivity} entities (separate from Profile aggregate).
 */
public interface ProfileActivityRepository extends Repository<ProfileActivity, Long> {

    Optional<ProfileActivity> findByProfileId(Long profileId);
}
