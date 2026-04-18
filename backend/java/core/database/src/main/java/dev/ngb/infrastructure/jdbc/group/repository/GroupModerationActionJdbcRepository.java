package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.moderation.GroupModerationAction;
import dev.ngb.domain.group.repository.GroupModerationActionRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupModerationActionJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupModerationActionJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupModerationActionJdbcRepository extends JdbcRepository<GroupModerationAction, GroupModerationActionJdbcEntity, Long> implements GroupModerationActionRepository {

    public GroupModerationActionJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupModerationActionJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupModerationActionJdbcMapper.INSTANCE);
    }

    @Override
    public List<GroupModerationAction> findByGroupId(Long groupId) {
        return findAllByFieldEqual("groupId", groupId);
    }

    @Override
    public List<GroupModerationAction> findByGroupIdAndTargetProfileId(Long groupId, Long targetProfileId) {
        return findAll(Criteria.where("groupId").is(groupId).and("targetProfileId").is(targetProfileId));
    }
}
