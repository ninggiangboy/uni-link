package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.stats.GroupStats;
import dev.ngb.domain.group.repository.GroupStatsRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupStatsJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupStatsJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GroupStatsJdbcRepository extends JdbcRepository<GroupStats, GroupStatsJdbcEntity, Long> implements GroupStatsRepository {

    public GroupStatsJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupStatsJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupStatsJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<GroupStats> findByGroupId(Long groupId) {
        return findFirstByFieldEqual("groupId", groupId);
    }
}
