package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.member.GroupMember;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupMemberJdbcEntity;

public final class GroupMemberJdbcMapper implements JdbcMapper<GroupMember, GroupMemberJdbcEntity> {

    public static final GroupMemberJdbcMapper INSTANCE = new GroupMemberJdbcMapper();

    private GroupMemberJdbcMapper() {}

    @Override
    public GroupMember toDomain(GroupMemberJdbcEntity entity) {
        return GroupMember.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getProfileId(),
                entity.getRole(),
                entity.getStatus(),
                entity.getMutedUntil(),
                entity.getPermissions()
        );
    }

    @Override
    public GroupMemberJdbcEntity toJdbc(GroupMember domain) {
        return GroupMemberJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .profileId(domain.getProfileId())
                .role(domain.getRole())
                .status(domain.getStatus())
                .mutedUntil(domain.getMutedUntil())
                .permissions(domain.getPermissions())
                .build();
    }
}
