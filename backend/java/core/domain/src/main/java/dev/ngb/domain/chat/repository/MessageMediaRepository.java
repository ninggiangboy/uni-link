package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.message.MessageMedia;

/**
 * Repository for managing {@link MessageMedia} entities.
 */
public interface MessageMediaRepository extends Repository<MessageMedia, Long> {
}
