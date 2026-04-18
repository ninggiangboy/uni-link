package dev.ngb.domain.identity.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.identity.model.auth.Account;

import java.util.Optional;

/**
 * Repository for managing {@link Account} aggregates.
 */
public interface AccountRepository extends Repository<Account, Long> {

    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);
}
