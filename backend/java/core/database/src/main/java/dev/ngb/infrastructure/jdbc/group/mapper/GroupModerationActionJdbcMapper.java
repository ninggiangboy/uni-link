package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.moderation.GroupModerationAction;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupModerationActionJdbcEntity;

public final class GroupModerationActionJdbcMapper implements JdbcMapper<GroupModerationAction, GroupModerationActionJdbcEntity> {

    public static final GroupModerationActionJdbcMapper INSTANCE = new GroupModerationActionJdbcMapper();

    private GroupModerationActionJdbcMapper() {}

    @Override
    public GroupModerationAction toDomain(GroupModerationActionJdbcEntity entity) {
        return GroupModerationAction.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getTargetProfileId(),
                entity.getModeratorProfileId(),
                entity.getActionType(),
                entity.getReason(),
                entity.getExpiresAt()
        );
    }

    @Override
    public GroupModerationActionJdbcEntity toJdbc(GroupModerationAction domain) {
        return GroupModerationActionJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .targetProfileId(domain.getTargetProfileId())
                .moderatorProfileId(domain.getModeratorProfileId())
                .actionType(domain.getActionType())
                .reason(domain.getReason())
                .expiresAt(domain.getExpiresAt())
                .build();
    }
}
