package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.conversation.ConversationReadState;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationReadStateJdbcEntity;

public final class ConversationReadStateJdbcMapper implements JdbcMapper<ConversationReadState, ConversationReadStateJdbcEntity> {

    public static final ConversationReadStateJdbcMapper INSTANCE = new ConversationReadStateJdbcMapper();

    private ConversationReadStateJdbcMapper() {}

    @Override
    public ConversationReadState toDomain(ConversationReadStateJdbcEntity entity) {
        return ConversationReadState.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getConversationId(),
                entity.getProfileId(),
                entity.getLastReadMessageId());
    }

    @Override
    public ConversationReadStateJdbcEntity toJdbc(ConversationReadState domain) {
        return ConversationReadStateJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .conversationId(domain.getConversationId())
                .profileId(domain.getProfileId())
                .lastReadMessageId(domain.getLastReadMessageId())
                .build();
    }
}
