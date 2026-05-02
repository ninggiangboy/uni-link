package dev.ngb.infrastructure.jdbc.profile.mapper;

import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.profile.entity.FollowRequestJdbcEntity;

public final class FollowRequestJdbcMapper implements JdbcMapper<FollowRequest, FollowRequestJdbcEntity> {

    public static final FollowRequestJdbcMapper INSTANCE = new FollowRequestJdbcMapper();

    private FollowRequestJdbcMapper() {}

    @Override
    public FollowRequest toDomain(FollowRequestJdbcEntity entity) {
        return FollowRequest.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getRequesterProfileId(),
                entity.getTargetProfileId(),
                entity.getStatus(),
                entity.getRespondedAt()
        );
    }

    @Override
    public FollowRequestJdbcEntity toJdbc(FollowRequest domain) {
        return FollowRequestJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .requesterProfileId(domain.getRequesterProfileId())
                .targetProfileId(domain.getTargetProfileId())
                .status(domain.getStatus())
                .respondedAt(domain.getRespondedAt())
                .build();
    }
}
