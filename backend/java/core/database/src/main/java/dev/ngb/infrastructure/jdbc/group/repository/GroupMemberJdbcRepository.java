package dev.ngb.infrastructure.jdbc.group.repository;

import dev.ngb.domain.group.model.member.GroupMember;
import dev.ngb.domain.group.repository.GroupMemberRepository;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import dev.ngb.infrastructure.jdbc.group.entity.GroupMemberJdbcEntity;
import dev.ngb.infrastructure.jdbc.group.mapper.GroupMemberJdbcMapper;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupMemberJdbcRepository extends JdbcRepository<GroupMember, GroupMemberJdbcEntity, Long> implements GroupMemberRepository {

    public GroupMemberJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(GroupMemberJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, GroupMemberJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<GroupMember> findByGroupIdAndProfileId(Long groupId, Long profileId) {
        return findFirst(Criteria.where("groupId").is(groupId).and("profileId").is(profileId));
    }

    @Override
    public List<GroupMember> findByGroupId(Long groupId) {
        return findAllByFieldEqual("groupId", groupId);
    }

    @Override
    public List<GroupMember> findByProfileId(Long profileId) {
        return findAllByFieldEqual("profileId", profileId);
    }

    @Override
    public boolean existsByGroupIdAndProfileId(Long groupId, Long profileId) {
        return exists(Criteria.where("groupId").is(groupId).and("profileId").is(profileId));
    }
}
