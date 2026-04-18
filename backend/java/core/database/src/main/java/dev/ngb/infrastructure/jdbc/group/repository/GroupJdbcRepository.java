package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.group.Group;
import dev.ngb.domain.group.repository.GroupRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GroupJdbcRepository extends JdbcRepository<Group, GroupJdbcEntity, Long> implements GroupRepository {

    public GroupJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<Group> findBySlug(String slug) {
        return findFirstByFieldEqual("slug", slug);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return existsByFieldEqual("slug", slug);
    }
}
