package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.conversation.ConversationReadState;

/**
 * Repository for managing {@link ConversationReadState} entities.
 */
public interface ConversationReadStateRepository extends Repository<ConversationReadState, Long> {
}
