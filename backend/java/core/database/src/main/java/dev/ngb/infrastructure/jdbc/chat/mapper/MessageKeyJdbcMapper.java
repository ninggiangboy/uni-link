package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.message.MessageKey;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageKeyJdbcEntity;

public final class MessageKeyJdbcMapper implements JdbcMapper<MessageKey, MessageKeyJdbcEntity> {

    public static final MessageKeyJdbcMapper INSTANCE = new MessageKeyJdbcMapper();

    private MessageKeyJdbcMapper() {}

    @Override
    public MessageKey toDomain(MessageKeyJdbcEntity entity) {
        return MessageKey.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getMessageId(),
                entity.getProfileId(),
                entity.getEncryptedKey()
        );
    }

    @Override
    public MessageKeyJdbcEntity toJdbc(MessageKey domain) {
        return MessageKeyJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .messageId(domain.getMessageId())
                .profileId(domain.getProfileId())
                .encryptedKey(domain.getEncryptedKey())
                .build();
    }
}
