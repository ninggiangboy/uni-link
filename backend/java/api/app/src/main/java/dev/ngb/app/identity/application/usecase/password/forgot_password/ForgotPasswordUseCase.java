package dev.ngb.app.identity.application.usecase.password.forgot_password;

import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.ForgotPasswordRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/*
 * Starts password reset by emailing a PASSWORD_RESET OTP when an active account exists for the
 * address. If there is no account or the account is not active, the use case returns without
 * error or email so callers cannot tell whether the mailbox is registered (enumeration resistance).
 * The web layer can still respond with a generic success to the client.
 *
 * ResetPassword later validates the OTP and applies the new password.
 */
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountOtpDeliveryService accountOtpDeliveryService;

    public void execute(ForgotPasswordRequest request) {
        log.info("Forgot password request (email masked for enumeration protection)");

        Optional<Account> accountOpt = accountRepository.findByEmail(request.email());
        // Deliberately identical outcome for unknown or inactive emails — do not leak registration state.
        if (accountOpt.isEmpty() || !accountOpt.get().isActive()) {
            log.debug("Forgot password: no active account for email, returning without sending OTP");
            return;
        }

        Account account = accountOpt.get();

        // ResetPassword will load latest active OTP with this purpose.
        accountOtpDeliveryService.sendEmailOtp(account.getId(), request.email(), OtpPurpose.PASSWORD_RESET);
    }
}
