package dev.ngb.domain.profile.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.profile.model.profile.ProfileLink;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link ProfileLink} entities (separate from Profile aggregate).
 */
public interface ProfileLinkRepository extends Repository<ProfileLink, Long> {

    List<ProfileLink> findByProfileId(Long profileId);

    Optional<ProfileLink> findByUuidAndProfileId(String uuid, Long profileId);

    long countByProfileId(Long profileId);
}
