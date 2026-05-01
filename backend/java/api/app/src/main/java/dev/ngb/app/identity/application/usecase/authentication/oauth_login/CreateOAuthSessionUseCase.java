package dev.ngb.app.identity.application.usecase.authentication.oauth_login;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.OAuthProviderVerifier;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionResponse;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountCredential;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.DeviceType;
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
public class CreateOAuthSessionUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountCredentialRepository accountCredentialRepository;
    private final AccountDeviceRepository accountDeviceRepository;
    private final OAuthProviderVerifier oAuthProviderVerifier;
    private final AccountSessionTokenService accountSessionTokenService;

    public CreateOAuthSessionResponse execute(CreateOAuthSessionRequest request, String ipAddress) {
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

        Optional<AccountCredential> credentialByProviderAccount = accountCredentialRepository
                .findByProviderAndProviderAccountId(request.provider(), userInfo.providerAccountId());

        Optional<Account> accountByEmail = accountRepository.findByEmail(userInfo.email());
        Optional<Account> accountLinkedByProviderSubject = Optional.empty();

        if (credentialByProviderAccount.isPresent()) {
            Long linkedAccountId = credentialByProviderAccount.get().getAccountId();
            Account linkedAccount = accountRepository.findById(linkedAccountId)
                    .orElseThrow(() -> {
                        log.warn("OAuth verification failed: linked account not found accountId={}", linkedAccountId);
                        return AccountError.INVALID_OAUTH_TOKEN.exception();
                    });
            accountLinkedByProviderSubject = Optional.of(linkedAccount);
        }

        Optional<AccountCredential> providerCredentialForEmailAccount = accountByEmail
                .flatMap(account -> accountCredentialRepository.findByAccountIdAndProvider(account.getId(), request.provider()));

        OAuthLoginContext loginContext = resolveOAuthLoginContext(
                accountByEmail.orElse(null),
                accountLinkedByProviderSubject.orElse(null),
                providerCredentialForEmailAccount.orElse(null),
                userInfo.providerAccountId()
        );

        OAuthAccountResolution resolution =
                decideOAuthAccountLinking(
                        loginContext.account(),
                        userInfo.email(),
                        loginContext.providerLinked()
                );

        Account account = resolution.account();
        boolean isNewAccount = resolution.newAccount();
        if (isNewAccount) {
            account = accountRepository.save(account);
        }

        if (resolution.shouldLinkProvider()) {
            AccountCredential credential = AccountCredential.create(account.getId(), request.provider(), userInfo.providerAccountId());
            accountCredentialRepository.save(credential);
        }

        // Same device model as password login, but provider trust skips email OTP for new devices.
        String fingerprint = request.deviceInfo().fingerprint();
        AccountDevice device = accountDeviceRepository
                .findByAccountIdAndFingerprint(account.getId(), fingerprint)
                .orElse(null);

        AccountDevice updatedDevice = decideDeviceForOAuthSignIn(
                account.getId(),
                device,
                request.deviceInfo().deviceType(),
                request.deviceInfo().deviceName(),
                fingerprint
        );
        device = accountDeviceRepository.save(updatedDevice);

        device.touch();
        account.recordLogin(ipAddress);
        account = accountRepository.save(account);

        // Full session immediately — symmetric with trusted password login.
        AuthTokenResponse tokens = accountSessionTokenService.createSessionAndIssueTokens(account, device.getId(), ipAddress);

        log.info("OAuth login successful accountId={}, accountUuid={}, isNewAccount={}", account.getId(), account.getUuid(), isNewAccount);
        return new CreateOAuthSessionResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresIn(),
                tokens.accountUuid(),
                isNewAccount
        );
    }

    private AccountDevice decideDeviceForOAuthSignIn(
            Long accountId,
            AccountDevice existingDevice,
            DeviceType deviceType,
            String deviceName,
            String fingerprint
    ) {
        if (existingDevice == null) {
            AccountDevice created = AccountDevice.create(accountId, deviceType, deviceName, fingerprint);
            created.markTrusted();
            return created;
        }
        existingDevice.touch();
        return existingDevice;
    }

    private OAuthLoginContext resolveOAuthLoginContext(
            Account accountByEmail,
            Account accountLinkedByProviderSubject,
            AccountCredential providerCredentialForEmailAccount,
            String providerAccountId
    ) {
        if (accountLinkedByProviderSubject != null) {
            if (accountByEmail != null && !accountByEmail.getId().equals(accountLinkedByProviderSubject.getId())) {
                throw AccountError.OAUTH_EMAIL_CONFLICT.exception();
            }
            return new OAuthLoginContext(accountLinkedByProviderSubject, true);
        }
        if (accountByEmail == null) {
            return new OAuthLoginContext(null, false);
        }
        if (providerCredentialForEmailAccount != null
                && !providerAccountId.equals(providerCredentialForEmailAccount.getProviderAccountId())) {
            throw AccountError.INVALID_OAUTH_TOKEN.exception();
        }
        return new OAuthLoginContext(accountByEmail, providerCredentialForEmailAccount != null);
    }

    private OAuthAccountResolution decideOAuthAccountLinking(Account existingAccount, String email, boolean providerLinked) {
        if (existingAccount == null) {
            return new OAuthAccountResolution(Account.createFromOAuth(email), true, true);
        }
        if (!existingAccount.isActive()) {
            throw AccountError.ACCOUNT_NOT_ACTIVE.exception();
        }
        return new OAuthAccountResolution(existingAccount, false, !providerLinked);
    }

    private record OAuthAccountResolution(Account account, boolean newAccount, boolean shouldLinkProvider) {
    }

    private record OAuthLoginContext(Account account, boolean providerLinked) {
    }
}
