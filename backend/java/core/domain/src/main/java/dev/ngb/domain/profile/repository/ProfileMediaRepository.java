package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.profile.ProfileMedia;

import java.util.List;

/**
 * Repository for managing {@link ProfileMedia} entities (separate from Profile aggregate).
 */
public interface ProfileMediaRepository extends Repository<ProfileMedia, Long> {

    List<ProfileMedia> findByProfileId(Long profileId);
}
