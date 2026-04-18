package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.moderation.ConversationBlock;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationBlockJdbcEntity;

public final class ConversationBlockJdbcMapper implements JdbcMapper<ConversationBlock, ConversationBlockJdbcEntity> {

    public static final ConversationBlockJdbcMapper INSTANCE = new ConversationBlockJdbcMapper();

    private ConversationBlockJdbcMapper() {}

    @Override
    public ConversationBlock toDomain(ConversationBlockJdbcEntity entity) {
        return ConversationBlock.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getBlockerProfileId(),
                entity.getBlockedProfileId(),
                entity.getConversationId()
        );
    }

    @Override
    public ConversationBlockJdbcEntity toJdbc(ConversationBlock domain) {
        return ConversationBlockJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .blockerProfileId(domain.getBlockerProfileId())
                .blockedProfileId(domain.getBlockedProfileId())
                .conversationId(domain.getConversationId())
                .build();
    }
}

