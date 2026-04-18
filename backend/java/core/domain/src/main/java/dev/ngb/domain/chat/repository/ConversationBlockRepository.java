package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.moderation.ConversationBlock;

/**
 * Repository for managing {@link ConversationBlock} cross-aggregate conversation blocks.
 */
public interface ConversationBlockRepository extends Repository<ConversationBlock, Long> {
}

