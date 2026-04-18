package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.conversation.Conversation;

/**
 * Repository for managing {@link Conversation} aggregates.
 */
public interface ConversationRepository extends Repository<Conversation, Long> {
}
