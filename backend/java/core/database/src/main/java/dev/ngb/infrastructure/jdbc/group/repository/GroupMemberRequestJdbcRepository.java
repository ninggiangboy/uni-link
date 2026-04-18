package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.membership.GroupMemberRequest;
import dev.ngb.domain.group.repository.GroupMemberRequestRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupMemberRequestJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupMemberRequestJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupMemberRequestJdbcRepository extends JdbcRepository<GroupMemberRequest, GroupMemberRequestJdbcEntity, Long> implements GroupMemberRequestRepository {

    public GroupMemberRequestJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupMemberRequestJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupMemberRequestJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<GroupMemberRequest> findByGroupIdAndRequesterProfileId(Long groupId, Long requesterProfileId) {
        return findFirst(Criteria.where("groupId").is(groupId).and("requesterProfileId").is(requesterProfileId));
    }

    @Override
    public List<GroupMemberRequest> findByGroupId(Long groupId) {
        return findAllByFieldEqual("groupId", groupId);
    }

    @Override
    public List<GroupMemberRequest> findByRequesterProfileId(Long requesterProfileId) {
        return findAllByFieldEqual("requesterProfileId", requesterProfileId);
    }

    @Override
    public boolean existsByGroupIdAndRequesterProfileId(Long groupId, Long requesterProfileId) {
        return exists(Criteria.where("groupId").is(groupId).and("requesterProfileId").is(requesterProfileId));
    }
}
