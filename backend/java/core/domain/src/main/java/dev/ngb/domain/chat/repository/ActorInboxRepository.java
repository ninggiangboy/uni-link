package dev.ngb.domain.chat.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.chat.model.inbox.ActorInbox;

/**
 * Repository for managing {@link ActorInbox} denormalized inbox rows.
 */
public interface ActorInboxRepository extends Repository<ActorInbox, Long> {
}
