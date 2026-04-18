package dev.ngb.domain.identity.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.identity.model.auth.AccountCredential;
import dev.ngb.domain.identity.model.auth.AuthProvider;

import java.util.List;

/**
 * Repository for managing {@link AccountCredential} entities (separate from Account aggregate).
 */
public interface AccountCredentialRepository extends Repository<AccountCredential, Long> {

    List<AccountCredential> findByAccountId(Long accountId);

    boolean existsByAccountIdAndProvider(Long accountId, AuthProvider provider);
}
