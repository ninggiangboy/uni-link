package dev.ngb.app.identity.application.usecase.registration.verify_email;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.service.AuthenticationPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Confirms registration by validating the OTP that was emailed after sign-up. The account must
 * still be pending; if it was already verified, the use case fails so the flow cannot be replayed.
 *
 * The latest active registration OTP is loaded and checked against the submitted code. On success
 * the account is activated and persisted. A device record is created from the request and marked
 * trusted, since proving inbox access is treated as strong enough for first login on that device.
 *
 * A new session is opened: the refresh token is stored hashed, and a short-lived access token is
 * issued—the same pattern as a successful password login. The client receives AuthTokenResponse
 * with tokens, access TTL, and the account UUID.
 */
@Slf4j
@RequiredArgsConstructor
public class CompleteEmailVerificationUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final AccountOtpRepository accountOtpRepository;
    private final AccountSessionTokenService accountSessionTokenService;
    private final AuthenticationPolicyService authenticationPolicyService;

    public AuthTokenResponse execute(String verificationId, CompleteEmailVerificationRequest request, String ipAddress) {
        log.info("Verify email attempt for verificationId={}", verificationId);

        // Unknown verification challenge cannot complete this flow.
        AccountOtp otp = accountOtpRepository.findByUuid(verificationId)
                .orElseThrow(() -> {
                    log.warn("Verify email failed: verification challenge not found");
                    return AccountError.INVALID_OTP.exception();
                });

        if (!OtpPurpose.REGISTRATION.equals(otp.getPurpose())) {
            log.warn("Verify email failed: OTP purpose mismatch purpose={}", otp.getPurpose());
            throw AccountError.INVALID_OTP.exception();
        }

        Account account = accountRepository.findById(otp.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Verify email failed: account not found for challenge");
                    return AccountError.ACCOUNT_NOT_FOUND.exception();
                });

        // Already verified accounts must not re-run activation or re-issue first-login tokens here.
        if (!account.isPending()) {
            log.warn("Verify email failed: email already verified accountId={}", account.getId());
            throw AccountError.EMAIL_ALREADY_VERIFIED.exception();
        }

        try {
            otp.verify(request.otpCode());
        } finally {
            // Persist failed-attempt counters even when verification fails.
            accountOtpRepository.save(otp);
        }
        log.debug("Registration OTP verified for accountId={}", account.getId());

        account.activate();
        accountRepository.save(account);

        // Inbox proof counts as strong enough to trust this device on first session.
        AccountDevice device = authenticationPolicyService.decideTrustedDeviceAfterEmailVerification(
                account.getId(),
                request.deviceInfo().deviceType(),
                request.deviceInfo().deviceName(),
                request.deviceInfo().fingerprint()
        );
        AccountDevice savedDevice = accountDeviceRepository.save(device);

        AuthTokenResponse tokens = accountSessionTokenService.createSessionAndIssueTokens(account, savedDevice.getId(), ipAddress);

        log.info("Verify email successful accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return tokens;
    }
}
