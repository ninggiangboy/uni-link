package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.conversation.Conversation;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationJdbcEntity;

public final class ConversationJdbcMapper implements JdbcMapper<Conversation, ConversationJdbcEntity> {

    public static final ConversationJdbcMapper INSTANCE = new ConversationJdbcMapper();

    private ConversationJdbcMapper() {}

    @Override
    public Conversation toDomain(ConversationJdbcEntity entity) {
        return Conversation.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getType(),
                entity.getCreatedByProfileId(),
                entity.getLastMessageId(),
                entity.getLastMessageAt()
        );
    }

    @Override
    public ConversationJdbcEntity toJdbc(Conversation domain) {
        return ConversationJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .type(domain.getType())
                .createdByProfileId(domain.getCreatedByProfileId())
                .lastMessageId(domain.getLastMessageId())
                .lastMessageAt(domain.getLastMessageAt())
                .build();
    }
}

