package dev.ngb.app.identity.application.usecase.registration.register_account;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountResponse;
import dev.ngb.application.UseCaseService;
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

        String passwordHash = passwordEncoder.encode(request.password());
        Account account = accountRepository.save(Account.create(request.email(), passwordHash));
        log.debug("Account created accountId={}", account.getId());

        AccountOtp otp = accountOtpDeliveryService.sendEmailOtp(account.getId(), request.email(), OtpPurpose.REGISTRATION);

        log.info("Register account successful accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return new CreateAccountResponse(account.getUuid(), otp.getUuid());
    }
}
