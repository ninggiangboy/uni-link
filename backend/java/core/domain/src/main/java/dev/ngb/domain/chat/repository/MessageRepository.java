package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.message.Message;

/**
 * Repository for managing {@link Message} aggregates.
 */
public interface MessageRepository extends Repository<Message, Long> {
}
