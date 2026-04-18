package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.content.GroupRule;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupRuleJdbcEntity;

public final class GroupRuleJdbcMapper implements JdbcMapper<GroupRule, GroupRuleJdbcEntity> {

    public static final GroupRuleJdbcMapper INSTANCE = new GroupRuleJdbcMapper();

    private GroupRuleJdbcMapper() {}

    @Override
    public GroupRule toDomain(GroupRuleJdbcEntity entity) {
        return GroupRule.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPosition()
        );
    }

    @Override
    public GroupRuleJdbcEntity toJdbc(GroupRule domain) {
        return GroupRuleJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .position(domain.getPosition())
                .build();
    }
}
