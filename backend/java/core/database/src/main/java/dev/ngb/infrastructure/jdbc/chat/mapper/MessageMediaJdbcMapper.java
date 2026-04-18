package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.message.MessageMedia;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageMediaJdbcEntity;

public final class MessageMediaJdbcMapper implements JdbcMapper<MessageMedia, MessageMediaJdbcEntity> {

    public static final MessageMediaJdbcMapper INSTANCE = new MessageMediaJdbcMapper();

    private MessageMediaJdbcMapper() {}

    @Override
    public MessageMedia toDomain(MessageMediaJdbcEntity entity) {
        return MessageMedia.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getMessageId(),
                entity.getMediaId());
    }

    @Override
    public MessageMediaJdbcEntity toJdbc(MessageMedia domain) {
        return MessageMediaJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .messageId(domain.getMessageId())
                .mediaId(domain.getMediaId())
                .build();
    }
}
