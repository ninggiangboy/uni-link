package dev.ngb.app.identity.application.usecase.authentication.verify_login;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.VerifyLoginRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountLoginHistory;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountLoginHistoryRepository;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Finishes password login after the email OTP step (new device or 2FA). The client sends the
 * verification token from LoginAccountResponse together with the OTP from email.
 *
 * The verification token encodes account and device ids; bad or tampered tokens fail with
 * INVALID_VERIFICATION_TOKEN. The latest active login OTP must match the submitted code. The device
 * must belong to that account; after a successful OTP it is marked trusted if it was not already.
 *
 * Login is recorded, success history is written, a new session stores the hashed refresh token,
 * and a fresh access token is returned in AuthTokenResponse.
 */
@Slf4j
@RequiredArgsConstructor
public class VerifyLoginUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final AccountOtpRepository accountOtpRepository;
    private final AccountLoginHistoryRepository accountLoginHistoryRepository;
    private final TokenProvider tokenProvider;
    private final AccountSessionTokenService accountSessionTokenService;

    public AuthTokenResponse execute(VerifyLoginRequest request, String ipAddress) {
        log.info("Verify login attempt");

        // Opaque token from LoginAccountUseCase; must decode to the same account/device the OTP was sent for.
        TokenProvider.VerificationClaims claims;
        try {
            claims = tokenProvider.parseVerificationToken(request.verificationToken());
        } catch (Exception e) {
            log.warn("Invalid verification token: {}", e.getMessage());
            throw AccountError.INVALID_VERIFICATION_TOKEN.exception();
        }

        log.debug("Verification token parsed accountId={}, deviceId={}", claims.accountId(), claims.deviceId());

        // Account might have been removed after the verification token was minted.
        Account account = accountRepository.findById(claims.accountId())
                .orElseThrow(() -> {
                    log.warn("Account not found for verify login accountId={}", claims.accountId());
                    return AccountError.ACCOUNT_NOT_FOUND.exception();
                });
        account.ensureCanLogin();

        Long accountId = account.getId();
        // Pairs with the LOGIN OTP emailed during sendVerificationAndRespond.
        AccountOtp otp = accountOtpRepository
                .findLatestActiveByAccountIdAndPurpose(accountId, OtpPurpose.LOGIN)
                .orElseThrow(() -> {
                    log.warn("No active login OTP for accountId={}", accountId);
                    return AccountError.INVALID_OTP.exception();
                });

        try {
            otp.verify(request.otpCode());
        } finally {
            // Persist attempts even on invalid OTP, so max-attempt protection cannot be bypassed.
            accountOtpRepository.save(otp);
        }
        log.debug("OTP verified for accountId={}", accountId);

        // Reject tokens that point at another account's device id.
        AccountDevice device = accountDeviceRepository.findById(claims.deviceId())
                .filter(d -> accountId.equals(d.getAccountId()))
                .orElseThrow(() -> {
                    log.warn("Device not found for verify login accountId={}, deviceId={}", accountId, claims.deviceId());
                    return AccountError.INVALID_VERIFICATION_TOKEN.exception();
                });

        if (!Boolean.TRUE.equals(device.getIsTrusted())) {
            // Post-OTP device is allowed to skip future inbox challenges until policy changes.
            log.debug("Marking device as trusted accountId={}, deviceId={}", accountId, device.getId());
            device.markTrusted();
            accountDeviceRepository.save(device);
        }

        account.recordLogin(ipAddress);
        account = accountRepository.save(account);

        // Mirror successful direct login: audit success with a concrete device id.
        accountLoginHistoryRepository.save(
                AccountLoginHistory.createSuccess(accountId, device.getId(), ipAddress, null)
        );

        AuthTokenResponse tokens = accountSessionTokenService.openSessionAndIssueTokens(account, device.getId(), ipAddress);

        log.info("Verify login successful accountId={}, accountUuid={}", accountId, account.getUuid());
        return tokens;
    }

}
