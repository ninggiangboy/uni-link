package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.content.GroupThread;
import dev.ngb.domain.group.repository.GroupThreadRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupThreadJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupThreadJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupThreadJdbcRepository extends JdbcRepository<GroupThread, GroupThreadJdbcEntity, Long> implements GroupThreadRepository {

    public GroupThreadJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupThreadJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupThreadJdbcMapper.INSTANCE);
    }

    @Override
    public List<GroupThread> findByGroupId(Long groupId) {
        return findAllByFieldEqual("groupId", groupId);
    }

    @Override
    public Optional<GroupThread> findByGroupIdAndThreadId(Long groupId, Long threadId) {
        return findFirst(Criteria.where("groupId").is(groupId).and("threadId").is(threadId));
    }

    @Override
    public boolean existsByGroupIdAndThreadId(Long groupId, Long threadId) {
        return exists(Criteria.where("groupId").is(groupId).and("threadId").is(threadId));
    }
}
