package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.profile.ProfileMetadata;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link ProfileMetadata} entities (separate from Profile aggregate).
 */
public interface ProfileMetadataRepository extends Repository<ProfileMetadata, Long> {

    List<ProfileMetadata> findByProfileId(Long profileId);

    Optional<ProfileMetadata> findByProfileIdAndKey(Long profileId, String key);
}
