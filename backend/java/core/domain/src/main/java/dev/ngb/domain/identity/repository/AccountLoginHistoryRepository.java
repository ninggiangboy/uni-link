package dev.ngb.domain.identity.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.identity.model.session.AccountLoginHistory;

/**
 * Repository for managing {@link AccountLoginHistory} audit records.
 */
public interface AccountLoginHistoryRepository extends Repository<AccountLoginHistory, Long> {
}
