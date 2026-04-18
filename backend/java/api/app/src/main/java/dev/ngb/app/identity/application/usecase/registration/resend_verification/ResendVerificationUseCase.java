package dev.ngb.app.identity.application.usecase.registration.resend_verification;

import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.ResendVerificationRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Sends another registration OTP when the user has not verified their email yet. A new code is
 * generated and emailed; password and account status stay unchanged aside from persisting the OTP.
 *
 * The account must exist and still be pending. If the address is unknown, ACCOUNT_NOT_FOUND is
 * returned; if the email was already verified, EMAIL_ALREADY_VERIFIED applies. VerifyEmail should
 * validate against the latest active registration OTP after this call.
 */
@Slf4j
@RequiredArgsConstructor
public class ResendVerificationUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountOtpDeliveryService accountOtpDeliveryService;

    public void execute(ResendVerificationRequest request) {
        log.info("Resend verification attempt for email={}", request.email() != null ? request.email().replaceAll("(?<=.).(?=.*@)", "*") : "***");

        // Same contract as verify: unknown mailbox is an error, not a silent success.
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Resend verification failed: account not found");
                    return AccountError.ACCOUNT_NOT_FOUND.exception();
                });

        // No point resending registration OTP after the account left pending.
        if (!account.isPending()) {
            log.warn("Resend verification failed: email already verified accountId={}", account.getId());
            throw AccountError.EMAIL_ALREADY_VERIFIED.exception();
        }

        // Fresh registration OTP for the next VerifyEmail call (queries use latest active by purpose).
        accountOtpDeliveryService.sendEmailOtp(account.getId(), request.email(), OtpPurpose.REGISTRATION);
    }
}
