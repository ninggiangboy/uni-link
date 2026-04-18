package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.membership.GroupInvitation;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link GroupInvitation} entities.
 */
public interface GroupInvitationRepository extends Repository<GroupInvitation, Long> {

    Optional<GroupInvitation> findByToken(String token);

    Optional<GroupInvitation> findByGroupIdAndInviteeProfileId(Long groupId, Long inviteeProfileId);

    List<GroupInvitation> findByGroupId(Long groupId);

    List<GroupInvitation> findByInviteeProfileId(Long inviteeProfileId);
}
