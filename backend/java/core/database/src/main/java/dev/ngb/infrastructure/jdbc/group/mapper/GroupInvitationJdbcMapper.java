package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.membership.GroupInvitation;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupInvitationJdbcEntity;

public final class GroupInvitationJdbcMapper implements JdbcMapper<GroupInvitation, GroupInvitationJdbcEntity> {

    public static final GroupInvitationJdbcMapper INSTANCE = new GroupInvitationJdbcMapper();

    private GroupInvitationJdbcMapper() {}

    @Override
    public GroupInvitation toDomain(GroupInvitationJdbcEntity entity) {
        return GroupInvitation.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getInviterProfileId(),
                entity.getInviteeProfileId(),
                entity.getStatus(),
                entity.getToken(),
                entity.getExpiresAt()
        );
    }

    @Override
    public GroupInvitationJdbcEntity toJdbc(GroupInvitation domain) {
        return GroupInvitationJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .inviterProfileId(domain.getInviterProfileId())
                .inviteeProfileId(domain.getInviteeProfileId())
                .status(domain.getStatus())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .build();
    }
}
