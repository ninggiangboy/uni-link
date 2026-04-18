package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.message.MessageReaction;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageReactionJdbcEntity;

public final class MessageReactionJdbcMapper implements JdbcMapper<MessageReaction, MessageReactionJdbcEntity> {

    public static final MessageReactionJdbcMapper INSTANCE = new MessageReactionJdbcMapper();

    private MessageReactionJdbcMapper() {}

    @Override
    public MessageReaction toDomain(MessageReactionJdbcEntity entity) {
        return MessageReaction.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getMessageId(),
                entity.getProfileId(),
                entity.getReaction());
    }

    @Override
    public MessageReactionJdbcEntity toJdbc(MessageReaction domain) {
        return MessageReactionJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .messageId(domain.getMessageId())
                .profileId(domain.getProfileId())
                .reaction(domain.getReaction())
                .build();
    }
}
