package dev.ngb.app.identity.application.usecase.password.reset_password;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import dev.ngb.domain.identity.service.PasswordResetDomainService;
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
public class CompletePasswordResetUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountOtpRepository accountOtpRepository;
    private final AccountSessionRepository accountSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetDomainService passwordResetDomainService;

    public void execute(String resetId, CompletePasswordResetRequest request) {
        log.info("Reset password attempt for resetId={}", resetId);

        AccountOtp otp = accountOtpRepository.findByUuid(resetId)
                .orElseThrow(() -> {
                    log.warn("Reset password failed: challenge not found");
                    return AccountError.INVALID_OTP.exception();
                });

        if (!OtpPurpose.PASSWORD_RESET.equals(otp.getPurpose())) {
            log.warn("Reset password failed: OTP purpose mismatch purpose={}", otp.getPurpose());
            throw AccountError.INVALID_OTP.exception();
        }

        Account account = accountRepository.findById(otp.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Reset password failed: account not found for challenge");
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
        passwordResetDomainService.revokeActiveSessions(activeSessions);
        accountSessionRepository.saveAll(activeSessions);
        log.info("Reset password successful accountId={}, revoked {} session(s)", account.getId(), activeSessions.size());
    }
}
