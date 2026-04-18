package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.group.Group;

import java.util.Optional;

/**
 * Repository for managing {@link Group} aggregates.
 */
public interface GroupRepository extends Repository<Group, Long> {

    Optional<Group> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
