package dev.ngb.app.identity.application.usecase.authentication.login_account;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountLoginHistory;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountLoginHistoryRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.service.AuthenticationPolicyService;
import dev.ngb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Password login with email and credential check. Missing accounts and wrong passwords both map to
 * the same error, so callers cannot infer whether an email is registered.
 *
 * After the password matches, account status is validated (pending, suspended, banned, deactivated
 * each fail with a specific domain error). The device is resolved by fingerprint. A brand-new device
 * always triggers email OTP plus a short-lived verification token instead of full tokens. The same
 * happens when two-factor is enabled on an existing device. Otherwise, the login is considered
 * trusted: login timestamps and success history are updated, then a session is created with fresh
 * access and refresh tokens.
 *
 * When OTP is required, a login-purpose OTP is persisted and emailed, and the response uses
 * verificationRequired so the client can complete the flow with VerifyLoginUseCase.
 */
@Slf4j
@RequiredArgsConstructor
public class LoginAccountUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final AccountLoginHistoryRepository accountLoginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AccountOtpDeliveryService accountOtpDeliveryService;
    private final AccountSessionTokenService accountSessionTokenService;
    private final AuthenticationPolicyService authenticationPolicyService;

    public LoginAccountResponse execute(LoginAccountRequest request, String ipAddress) {
        log.info("Login attempt for email={}", StringUtils.maskEmail(request.email()));

        // Same error as wrong password to avoid leaking whether the email is registered.
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed: account not found for email={}", StringUtils.maskEmail(request.email()));
                    return AccountError.INVALID_CREDENTIALS.exception();
                });

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            log.warn("Login failed: invalid password for accountId={}", account.getId());
            // Audit trail without a device id yet.
            accountLoginHistoryRepository.save(
                    AccountLoginHistory.createFailure(account.getId(), null, ipAddress, null, "Invalid password")
            );
            throw AccountError.INVALID_CREDENTIALS.exception();
        }

        log.debug("Password verified for accountId={}", account.getId());
        account.ensureCanLogin();

        // Stable client fingerprint ties sessions and OTP verification to one device row.
        String fingerprint = request.deviceInfo().fingerprint();
        AccountDevice existingDevice = accountDeviceRepository
                .findByAccountIdAndFingerprint(account.getId(), fingerprint)
                .orElse(null);

        AuthenticationPolicyService.LoginDecision decision =
                authenticationPolicyService.decidePasswordLogin(account, existingDevice);
        log.debug("accountId={}, loginDecision={}", account.getId(), decision);

        boolean requiresVerification = decision == AuthenticationPolicyService.LoginDecision.REQUIRE_VERIFICATION_NEW_DEVICE
                || decision == AuthenticationPolicyService.LoginDecision.REQUIRE_VERIFICATION_2FA;
        if (requiresVerification) {
            AccountDevice verificationDevice = authenticationPolicyService.decideDeviceForLoginVerification(
                    account.getId(),
                    existingDevice,
                    request.deviceInfo().deviceType(),
                    request.deviceInfo().deviceName(),
                    fingerprint
            );
            AccountDevice savedDevice = accountDeviceRepository.save(verificationDevice);
            log.info(
                    "Password login requires verification accountId={}, deviceId={}, decision={}",
                    account.getId(),
                    savedDevice.getId(),
                    decision
            );
            return issueLoginVerificationChallenge(account, savedDevice);
        }

        // Trusted path: update activity, record success, then mint session + tokens.
        authenticationPolicyService.applySuccessfulLogin(account, existingDevice, ipAddress, false);
        account = accountRepository.save(account);
        AccountDevice savedDevice = accountDeviceRepository.save(existingDevice);

        // Successful password login on a known, non-2FA device.
        accountLoginHistoryRepository.save(
                AccountLoginHistory.createSuccess(account.getId(), savedDevice.getId(), ipAddress, null)
        );

        var tokens = accountSessionTokenService.createSessionAndIssueTokens(account, savedDevice.getId(), ipAddress);

        log.info("Login successful for accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return LoginAccountResponse.authenticated(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                tokens.accountUuid()
        );
    }

    private LoginAccountResponse issueLoginVerificationChallenge(Account account, AccountDevice device) {
        accountOtpDeliveryService.sendEmailOtp(account.getId(), account.getEmail(), OtpPurpose.LOGIN);

        // Binds the email OTP step to this account + device for VerifyLoginUseCase.
        String verificationToken = tokenProvider.generateVerificationToken(account.getId(), device.getId());
        return LoginAccountResponse.verificationRequired(verificationToken);
    }
}
