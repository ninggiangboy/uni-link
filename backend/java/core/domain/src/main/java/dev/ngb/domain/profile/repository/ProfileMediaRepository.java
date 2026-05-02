package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.profile.ProfileMedia;
import dev.ngb.domain.profile.model.profile.ProfileMediaType;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link ProfileMedia} entities (separate from Profile aggregate).
 */
public interface ProfileMediaRepository extends Repository<ProfileMedia, Long> {

    List<ProfileMedia> findByProfileId(Long profileId);

    Optional<ProfileMedia> findLatestByProfileIdAndType(Long profileId, ProfileMediaType type);
}
