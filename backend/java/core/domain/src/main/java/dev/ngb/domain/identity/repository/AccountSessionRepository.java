package dev.ngb.domain.identity.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.identity.model.session.AccountSession;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link AccountSession} entities.
 */
public interface AccountSessionRepository extends Repository<AccountSession, Long> {

    Optional<AccountSession> findByTokenHash(String tokenHash);

    List<AccountSession> findActiveByAccountId(Long accountId);
}
