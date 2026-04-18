package dev.ngb.domain.identity.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.identity.model.auth.AccountDevice;

import java.util.Optional;

/**
 * Repository for managing {@link AccountDevice} entities.
 */
public interface AccountDeviceRepository extends Repository<AccountDevice, Long> {

    Optional<AccountDevice> findByAccountIdAndFingerprint(Long accountId, String fingerprint);
}

