package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.inbox.ActorInbox;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.ActorInboxJdbcEntity;

public final class ActorInboxJdbcMapper implements JdbcMapper<ActorInbox, ActorInboxJdbcEntity> {

    public static final ActorInboxJdbcMapper INSTANCE = new ActorInboxJdbcMapper();

    private ActorInboxJdbcMapper() {}

    @Override
    public ActorInbox toDomain(ActorInboxJdbcEntity entity) {
        return ActorInbox.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getProfileId(),
                entity.getConversationId(),
                entity.getLastMessageId(),
                entity.getLastReadMessageId(),
                entity.getUnreadCount(),
                entity.getPinned(),
                entity.getMuted()
        );
    }

    @Override
    public ActorInboxJdbcEntity toJdbc(ActorInbox domain) {
        return ActorInboxJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .profileId(domain.getProfileId())
                .conversationId(domain.getConversationId())
                .lastMessageId(domain.getLastMessageId())
                .lastReadMessageId(domain.getLastReadMessageId())
                .unreadCount(domain.getUnreadCount())
                .pinned(domain.getPinned())
                .muted(domain.getMuted())
                .build();
    }
}

