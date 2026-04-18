package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.membership.GroupMemberRequest;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupMemberRequestJdbcEntity;

public final class GroupMemberRequestJdbcMapper implements JdbcMapper<GroupMemberRequest, GroupMemberRequestJdbcEntity> {

    public static final GroupMemberRequestJdbcMapper INSTANCE = new GroupMemberRequestJdbcMapper();

    private GroupMemberRequestJdbcMapper() {}

    @Override
    public GroupMemberRequest toDomain(GroupMemberRequestJdbcEntity entity) {
        return GroupMemberRequest.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getRequesterProfileId(),
                entity.getStatus(),
                entity.getReviewedByProfileId(),
                entity.getReviewNote()
        );
    }

    @Override
    public GroupMemberRequestJdbcEntity toJdbc(GroupMemberRequest domain) {
        return GroupMemberRequestJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .requesterProfileId(domain.getRequesterProfileId())
                .status(domain.getStatus())
                .reviewedByProfileId(domain.getReviewedByProfileId())
                .reviewNote(domain.getReviewNote())
                .build();
    }
}
