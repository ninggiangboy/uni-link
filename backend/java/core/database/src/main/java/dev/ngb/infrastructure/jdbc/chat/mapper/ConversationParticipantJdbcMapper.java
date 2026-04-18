package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.conversation.ConversationParticipant;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.ConversationParticipantJdbcEntity;

public final class ConversationParticipantJdbcMapper implements JdbcMapper<ConversationParticipant, ConversationParticipantJdbcEntity> {

    public static final ConversationParticipantJdbcMapper INSTANCE = new ConversationParticipantJdbcMapper();

    private ConversationParticipantJdbcMapper() {}

    @Override
    public ConversationParticipant toDomain(ConversationParticipantJdbcEntity entity) {
        return ConversationParticipant.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getConversationId(),
                entity.getProfileId(),
                entity.getRole(),
                entity.getJoinedAt(),
                entity.getLeftAt());
    }

    @Override
    public ConversationParticipantJdbcEntity toJdbc(ConversationParticipant domain) {
        return ConversationParticipantJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .conversationId(domain.getConversationId())
                .profileId(domain.getProfileId())
                .role(domain.getRole())
                .joinedAt(domain.getJoinedAt())
                .leftAt(domain.getLeftAt())
                .build();
    }
}
