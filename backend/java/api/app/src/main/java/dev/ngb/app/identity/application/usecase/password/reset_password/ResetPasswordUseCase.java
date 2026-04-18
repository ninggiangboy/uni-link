package dev.ngb.app.identity.application.usecase.password.reset_password;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.ResetPasswordRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/*
 * Completes password reset using the OTP from ForgotPassword. The account is loaded by email,
 * the latest active password-reset OTP is checked against the submitted code, then the new password
 * is hashed and applied through the domain model.
 *
 * Every active session for that account is revoked so stolen refresh tokens cannot keep working
 * after a successful reset; users must sign in again everywhere.
 */
@Slf4j
@RequiredArgsConstructor
public class ResetPasswordUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountOtpRepository accountOtpRepository;
    private final AccountSessionRepository accountSessionRepository;
    private final PasswordEncoder passwordEncoder;

    public void execute(ResetPasswordRequest request) {
        log.info("Reset password attempt for email={}", request.email() != null ? request.email().replaceAll("(?<=.).(?=.*@)", "*") : "***");

        // Unlike forgot-password, invalid email is an error: user is past the enumeration-safe entrypoint.
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Reset password failed: invalid email or OTP");
                    return AccountError.INVALID_OTP.exception();
                });

        // Must match a code still valid in domain terms (attempt limits, expiry, etc.).
        AccountOtp otp = accountOtpRepository
                .findLatestActiveByAccountIdAndPurpose(account.getId(), OtpPurpose.PASSWORD_RESET)
                .orElseThrow(() -> {
                    log.warn("Reset password failed: no active OTP for accountId={}", account.getId());
                    return AccountError.INVALID_OTP.exception();
                });

        try {
            otp.verify(request.otpCode());
        } finally {
            // Persist attempts even for invalid OTP submissions.
            accountOtpRepository.save(otp);
        }
        log.debug("Password reset OTP verified for accountId={}", account.getId());

        String newPasswordHash = passwordEncoder.encode(request.newPassword());
        // Domain aggregate owns password transition rules.
        account.changePassword(newPasswordHash);
        accountRepository.save(account);

        // Invalidate outstanding refresh tokens so old clients cannot keep refreshing.
        List<AccountSession> activeSessions = accountSessionRepository.findActiveByAccountId(account.getId());
        activeSessions.forEach(AccountSession::revoke);
        accountSessionRepository.saveAll(activeSessions);
        log.info("Reset password successful accountId={}, revoked {} session(s)", account.getId(), activeSessions.size());
    }
}
