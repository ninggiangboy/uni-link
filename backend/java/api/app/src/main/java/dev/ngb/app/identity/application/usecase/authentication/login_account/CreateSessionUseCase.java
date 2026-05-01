package dev.ngb.app.identity.application.usecase.authentication.login_account;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountLoginHistory;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountLoginHistoryRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
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
 * verificationRequired so the client can complete the flow with CompleteSessionVerificationUseCase.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateSessionUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final AccountLoginHistoryRepository accountLoginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AccountOtpDeliveryService accountOtpDeliveryService;
    private final AccountSessionTokenService accountSessionTokenService;

    public CreateSessionResponse execute(CreateSessionRequest request, String ipAddress) {
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
        try {
            account.ensureCanLogin();
        } catch (DomainException e) {
            accountLoginHistoryRepository.save(
                    AccountLoginHistory.createBlocked(account.getId(), null, ipAddress, null, e.getError().getMessage())
            );
            throw e;
        }

        // Stable client fingerprint ties sessions and OTP verification to one device row.
        String fingerprint = request.deviceInfo().fingerprint();
        AccountDevice existingDevice = accountDeviceRepository
                .findByAccountIdAndFingerprint(account.getId(), fingerprint)
                .orElse(null);

        LoginDecision decision = decidePasswordLogin(account, existingDevice);
        log.debug("accountId={}, loginDecision={}", account.getId(), decision);

        boolean requiresVerification = decision == LoginDecision.REQUIRE_VERIFICATION_NEW_DEVICE
                || decision == LoginDecision.REQUIRE_VERIFICATION_2FA;
        if (requiresVerification) {
            AccountDevice verificationDevice = decideDeviceForLoginVerification(
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
        existingDevice.touch();
        account.recordLogin(ipAddress);
        account = accountRepository.save(account);
        AccountDevice savedDevice = accountDeviceRepository.save(existingDevice);

        // Successful password login on a known, non-2FA device.
        accountLoginHistoryRepository.save(
                AccountLoginHistory.createSuccess(account.getId(), savedDevice.getId(), ipAddress, null)
        );

        var tokens = accountSessionTokenService.createSessionAndIssueTokens(account, savedDevice.getId(), ipAddress);

        log.info("Login successful for accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return CreateSessionResponse.authenticated(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                tokens.accountUuid()
        );
    }

    private CreateSessionResponse issueLoginVerificationChallenge(Account account, AccountDevice device) {
        AccountOtp otp = accountOtpDeliveryService.sendEmailOtp(account.getId(), account.getEmail(), OtpPurpose.LOGIN);

        // Binds the email OTP step to this account + device for CompleteSessionVerificationUseCase.
        String verificationToken = tokenProvider.generateVerificationToken(account.getId(), device.getId(), otp.getUuid());
        return CreateSessionResponse.verificationRequired(verificationToken);
    }

    private LoginDecision decidePasswordLogin(Account account, AccountDevice existingDevice) {
        if (existingDevice == null) {
            return LoginDecision.REQUIRE_VERIFICATION_NEW_DEVICE;
        }
        if (Boolean.TRUE.equals(account.getTwoFactorEnabled())) {
            return LoginDecision.REQUIRE_VERIFICATION_2FA;
        }
        return LoginDecision.TRUSTED_DEVICE_DIRECT_AUTH;
    }

    private AccountDevice decideDeviceForLoginVerification(
            Long accountId,
            AccountDevice existingDevice,
            DeviceType deviceType,
            String deviceName,
            String fingerprint
    ) {
        if (existingDevice == null) {
            return AccountDevice.create(accountId, deviceType, deviceName, fingerprint);
        }
        existingDevice.touch();
        return existingDevice;
    }

    private enum LoginDecision {
        REQUIRE_VERIFICATION_NEW_DEVICE,
        REQUIRE_VERIFICATION_2FA,
        TRUSTED_DEVICE_DIRECT_AUTH
    }
}
