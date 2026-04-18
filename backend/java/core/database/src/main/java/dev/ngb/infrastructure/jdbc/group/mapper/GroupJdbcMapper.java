package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.group.Group;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupJdbcEntity;

public final class GroupJdbcMapper implements JdbcMapper<Group, GroupJdbcEntity> {

    public static final GroupJdbcMapper INSTANCE = new GroupJdbcMapper();

    private GroupJdbcMapper() {}

    @Override
    public Group toDomain(GroupJdbcEntity entity) {
        return Group.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getOwnerProfileId(),
                entity.getName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getAvatarUrl(),
                entity.getBannerUrl(),
                entity.getVisibility(),
                entity.getStatus(),
                entity.getRequiresApproval()
        );
    }

    @Override
    public GroupJdbcEntity toJdbc(Group domain) {
        return GroupJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .ownerProfileId(domain.getOwnerProfileId())
                .name(domain.getName())
                .slug(domain.getSlug())
                .description(domain.getDescription())
                .avatarUrl(domain.getAvatarUrl())
                .bannerUrl(domain.getBannerUrl())
                .visibility(domain.getVisibility())
                .status(domain.getStatus())
                .requiresApproval(domain.getRequiresApproval())
                .build();
    }
}
