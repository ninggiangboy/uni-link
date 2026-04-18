package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.message.MessageReaction;

/**
 * Repository for managing {@link MessageReaction} entities.
 */
public interface MessageReactionRepository extends Repository<MessageReaction, Long> {
}
