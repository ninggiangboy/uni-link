package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.message.MessageKey;

/**
 * Repository for managing {@link MessageKey} per-recipient encrypted keys.
 */
public interface MessageKeyRepository extends Repository<MessageKey, Long> {
}
