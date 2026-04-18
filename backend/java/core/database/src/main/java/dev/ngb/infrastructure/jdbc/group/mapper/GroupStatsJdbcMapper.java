package dev.ngb.infrastructure.jdbc.group.mapper;

import dev.ngb.domain.group.model.stats.GroupStats;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.group.entity.GroupStatsJdbcEntity;

public final class GroupStatsJdbcMapper implements JdbcMapper<GroupStats, GroupStatsJdbcEntity> {

    public static final GroupStatsJdbcMapper INSTANCE = new GroupStatsJdbcMapper();

    private GroupStatsJdbcMapper() {}

    @Override
    public GroupStats toDomain(GroupStatsJdbcEntity entity) {
        return GroupStats.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getGroupId(),
                entity.getMemberCount(),
                entity.getThreadCount()
        );
    }

    @Override
    public GroupStatsJdbcEntity toJdbc(GroupStats domain) {
        return GroupStatsJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .groupId(domain.getGroupId())
                .memberCount(domain.getMemberCount())
                .threadCount(domain.getThreadCount())
                .build();
    }
}
