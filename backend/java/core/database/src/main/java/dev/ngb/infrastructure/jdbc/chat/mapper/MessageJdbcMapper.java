package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.message.Message;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageJdbcEntity;

public final class MessageJdbcMapper implements JdbcMapper<Message, MessageJdbcEntity> {

    public static final MessageJdbcMapper INSTANCE = new MessageJdbcMapper();

    private MessageJdbcMapper() {}

    @Override
    public Message toDomain(MessageJdbcEntity entity) {
        return Message.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getConversationId(),
                entity.getSenderProfileId(),
                entity.getType(),
                entity.getContent(),
                entity.getIv(),
                entity.getEditedAt(),
                entity.getDeletedAt()
        );
    }

    @Override
    public MessageJdbcEntity toJdbc(Message domain) {
        return MessageJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .conversationId(domain.getConversationId())
                .senderProfileId(domain.getSenderProfileId())
                .type(domain.getType())
                .content(domain.getContent())
                .iv(domain.getIv())
                .editedAt(domain.getEditedAt())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}

