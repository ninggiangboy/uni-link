package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.conversation.ConversationParticipant;

/**
 * Repository for managing {@link ConversationParticipant} entities.
 */
public interface ConversationParticipantRepository extends Repository<ConversationParticipant, Long> {
}
