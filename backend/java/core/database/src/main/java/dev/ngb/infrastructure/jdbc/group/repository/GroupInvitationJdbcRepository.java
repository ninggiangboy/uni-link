package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.membership.GroupInvitation;
import dev.ngb.domain.group.repository.GroupInvitationRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupInvitationJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupInvitationJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupInvitationJdbcRepository extends JdbcRepository<GroupInvitation, GroupInvitationJdbcEntity, Long> implements GroupInvitationRepository {

    public GroupInvitationJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupInvitationJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupInvitationJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<GroupInvitation> findByToken(String token) {
        return findFirstByFieldEqual("token", token);
    }

    @Override
    public Optional<GroupInvitation> findByGroupIdAndInviteeProfileId(Long groupId, Long inviteeProfileId) {
        return findFirst(Criteria.where("group_id").is(groupId).and("invitee_profile_id").is(inviteeProfileId));
    }

    @Override
    public List<GroupInvitation> findByGroupId(Long groupId) {
        return findAllByFieldEqual("group_id", groupId);
    }

    @Override
    public List<GroupInvitation> findByInviteeProfileId(Long inviteeProfileId) {
        return findAllByFieldEqual("invitee_profile_id", inviteeProfileId);
    }
}
