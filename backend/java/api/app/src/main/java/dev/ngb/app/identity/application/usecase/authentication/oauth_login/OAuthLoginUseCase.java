package dev.ngb.app.identity.application.usecase.authentication.oauth_login;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.OAuthProviderVerifier;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountCredential;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.repository.AccountCredentialRepository;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/*
 * Signs the user in—or creates an account—using an OAuth access token the client obtained from the
 * provider. OAuthProviderVerifier turns that token into a trusted email and provider subject id.
 * Invalid provider tokens fail with INVALID_OAUTH_TOKEN.
 *
 * If no local account exists for the email, a new active account is created and linked with
 * AccountCredential. If an account already exists, it must be active; otherwise login is rejected.
 * When the provider is not yet linked, a new credential row links it to the existing account.
 *
 * Device handling uses the same fingerprint idea as password login, but new devices are marked
 * trusted immediately because identity was asserted by the OAuth provider. Login is recorded,
 * a session is opened, and tokens are returned. isNewAccount tells the UI whether onboarding
 * might be needed.
 */
@Slf4j
@RequiredArgsConstructor
public class OAuthLoginUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountCredentialRepository accountCredentialRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final OAuthProviderVerifier oAuthProviderVerifier;
    private final AccountSessionTokenService accountSessionTokenService;

    public OAuthLoginResponse execute(OAuthLoginRequest request, String ipAddress) {
        log.info("OAuth login attempt provider={}", request.provider());

        // Exchange provider token for normalized identity; failures are treated as bad OAuth input.
        OAuthProviderVerifier.OAuthUserInfo userInfo;
        try {
            userInfo = oAuthProviderVerifier.verify(request.provider(), request.providerToken());
        } catch (Exception e) {
            log.warn("OAuth verification failed provider={}: {}", request.provider(), e.getMessage());
            throw AccountError.INVALID_OAUTH_TOKEN.exception();
        }

        log.debug("OAuth user verified email={}", userInfo.email());

        // Email is the primary join key between IdP and our Account aggregate.
        Optional<Account> existingAccount = accountRepository.findByEmail(userInfo.email());
        boolean isNewAccount = existingAccount.isEmpty();

        Account account;
        if (isNewAccount) {
            // OAuth-verified email: start as active (no separate email-OTP registration step).
            log.debug("Creating new account from OAuth for email={}", userInfo.email());
            account = Account.createFromOAuth(userInfo.email());
            account = accountRepository.save(account);
            AccountCredential credential = AccountCredential.create(
                    account.getId(), request.provider(), userInfo.providerAccountId(),
                    null, null
            );
            accountCredentialRepository.save(credential);
        } else {
            account = existingAccount.get();
            // Local lifecycle still applies; OAuth cannot revive suspended or banned users.
            if (!account.isActive()) {
                log.warn("OAuth login rejected: account not active accountId={}", account.getId());
                throw AccountError.ACCOUNT_NOT_ACTIVE.exception();
            }
            // First time this provider is used for an existing password account — link rows.
            if (!accountCredentialRepository.existsByAccountIdAndProvider(account.getId(), request.provider())) {
                log.debug("Linking OAuth provider to existing account accountId={}", account.getId());
                AccountCredential credential = AccountCredential.create(
                        account.getId(), request.provider(), userInfo.providerAccountId(),
                        null, null
                );
                accountCredentialRepository.save(credential);
            }
        }

        // Same device model as password login, but provider trust skips email OTP for new devices.
        String fingerprint = request.deviceInfo().fingerprint();
        AccountDevice device = accountDeviceRepository
                .findByAccountIdAndFingerprint(account.getId(), fingerprint)
                .orElse(null);

        // Reuse device row when the client fingerprint was seen before.
        if (device == null) {
            log.debug("New device for OAuth login accountId={}", account.getId());
            AccountDevice newDevice = AccountDevice.create(
                    account.getId(),
                    request.deviceInfo().deviceType(),
                    request.deviceInfo().deviceName(),
                    fingerprint
            );
            // Provider already asserted identity; no extra inbox step here.
            newDevice.markTrusted();
            device = accountDeviceRepository.save(newDevice);
        } else {
            device.touch();
            device = accountDeviceRepository.save(device);
        }

        account.recordLogin(ipAddress);
        account = accountRepository.save(account);

        // Full session immediately — symmetric with trusted password login.
        AuthTokenResponse tokens = accountSessionTokenService.openSessionAndIssueTokens(account, device.getId(), ipAddress);

        log.info("OAuth login successful accountId={}, accountUuid={}, isNewAccount={}", account.getId(), account.getUuid(), isNewAccount);
        return new OAuthLoginResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                tokens.accountUuid(),
                isNewAccount
        );
    }
}
