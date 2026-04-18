package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.content.GroupThread;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupThreadJdbcEntity;

public final class GroupThreadJdbcMapper implements JdbcMapper<GroupThread, GroupThreadJdbcEntity> {

    public static final GroupThreadJdbcMapper INSTANCE = new GroupThreadJdbcMapper();

    private GroupThreadJdbcMapper() {}

    @Override
    public GroupThread toDomain(GroupThreadJdbcEntity entity) {
        return GroupThread.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getThreadId(),
                entity.getIsPinned()
        );
    }

    @Override
    public GroupThreadJdbcEntity toJdbc(GroupThread domain) {
        return GroupThreadJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .threadId(domain.getThreadId())
                .isPinned(domain.getIsPinned())
                .build();
    }
}
