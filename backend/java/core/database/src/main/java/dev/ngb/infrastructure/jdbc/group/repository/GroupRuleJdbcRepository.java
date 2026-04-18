package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.content.GroupRule;
import dev.ngb.domain.group.repository.GroupRuleRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupRuleJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupRuleJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupRuleJdbcRepository extends JdbcRepository<GroupRule, GroupRuleJdbcEntity, Long> implements GroupRuleRepository {

    public GroupRuleJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupRuleJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupRuleJdbcMapper.INSTANCE);
    }

    @Override
    public List<GroupRule> findByGroupId(Long groupId) {
        return findAll(
                org.springframework.data.relational.core.query.Criteria.where("groupId").is(groupId),
                org.springframework.data.domain.Pageable.unpaged(Sort.by(Sort.Direction.ASC, "position"))
        );
    }
}
