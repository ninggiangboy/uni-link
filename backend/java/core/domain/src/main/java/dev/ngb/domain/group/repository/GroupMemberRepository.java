package dev.ngb.domain.group.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.group.model.member.GroupMember;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link GroupMember} entities.
 */
public interface GroupMemberRepository extends Repository<GroupMember, Long> {

    Optional<GroupMember> findByGroupIdAndProfileId(Long groupId, Long profileId);

    List<GroupMember> findByGroupId(Long groupId);

    List<GroupMember> findByProfileId(Long profileId);

    boolean existsByGroupIdAndProfileId(Long groupId, Long profileId);
}
