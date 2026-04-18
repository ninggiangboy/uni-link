package dev.ngb.domain.identity.repository;

import dev.ngb.domain.Repository;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;

import java.util.Optional;

/**
 * Repository for managing {@link AccountOtp} entities.
 */
public interface AccountOtpRepository extends Repository<AccountOtp, Long> {

    Optional<AccountOtp> findLatestActiveByAccountIdAndPurpose(Long accountId, OtpPurpose purpose);
}
