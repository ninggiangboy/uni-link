package dev.ngb.app.identity.application.usecase.registration.register_account;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Registers a new account with email and password. The account stays pending until the user
 * completes email verification. Passwords are hashed through PasswordEncoder before persistence;
 * plaintext is never stored.
 *
 * Duplicate emails are rejected with a domain error. After the account is saved, a registration
 * OTP is generated, stored for later verification, and emailed. The response exposes only the
 * public account UUID, not the internal database id.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateAccountUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountOtpDeliveryService accountOtpDeliveryService;

    public CreateAccountResponse execute(CreateAccountRequest request) {
        log.info("Register account attempt for email={}", StringUtils.maskEmail(request.email()));

        // Fast path for common duplicates; save-time catch still handles race conditions.
        if (accountRepository.existsByEmail(request.email())) {
            log.warn("Register failed: email already exists");
            throw AccountError.EMAIL_ALREADY_EXISTS.exception();
        }

        // Hash before persistence; domain factory builds a pending account until email is verified.
        String passwordHash = passwordEncoder.encode(request.password());
        Account account = Account.create(request.email(), passwordHash);
        account = accountRepository.save(account);
        log.debug("Account created accountId={}", account.getId());

        AccountOtp otp = accountOtpDeliveryService.sendEmailOtp(account.getId(), request.email(), OtpPurpose.REGISTRATION);

        log.info("Register account successful accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return new CreateAccountResponse(account.getUuid(), otp.getUuid());
    }
}
