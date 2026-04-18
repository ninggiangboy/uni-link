package dev.ngb.app.identity.application.usecase.registration.register_account;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountRepository;
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
public class RegisterAccountUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountOtpDeliveryService accountOtpDeliveryService;

    public RegisterAccountResponse execute(RegisterAccountRequest request) {
        log.info("Register account attempt for email={}", request.email() != null ? request.email().replaceAll("(?<=.).(?=.*@)", "*") : "***");

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

        accountOtpDeliveryService.sendEmailOtp(account.getId(), request.email(), OtpPurpose.REGISTRATION);

        log.info("Register account successful accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return new RegisterAccountResponse(account.getUuid());
    }
}
