package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.OAuthProviderVerifier;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.OAuthLoginUseCase;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.domain.identity.repository.AccountCredentialRepository;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuthLoginUseCase")
class OAuthLoginUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountCredentialRepository accountCredentialRepository;
    @Mock
    private AccountDeviceRepository accountDeviceRepository;
    @Mock
    private OAuthProviderVerifier oAuthProviderVerifier;
    @Mock
    private AccountSessionTokenService accountSessionTokenService;

    @InjectMocks
    private OAuthLoginUseCase useCase;

    private OAuthLoginRequest request() {
        return new OAuthLoginRequest(AuthProvider.GOOGLE, "provider-jwt", IdentityUseCaseTestFixtures.device());
    }

    @Test
    @DisplayName("Provider verifier fails → INVALID_OAUTH_TOKEN")
    void executeWhenVerifierFailsThrowsInvalidOAuthToken() {
        when(oAuthProviderVerifier.verify(AuthProvider.GOOGLE, "provider-jwt"))
                .thenThrow(new RuntimeException("invalid"));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(request(), IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OAUTH_TOKEN);
    }

    @Test
    @DisplayName("New email → create account, link provider, issue tokens")
    void executeWhenNewEmailCreatesAccountLinksProviderOpensSession() {
        var userInfo = new OAuthProviderVerifier.OAuthUserInfo(IdentityUseCaseTestFixtures.EMAIL, "sub-1");
        when(oAuthProviderVerifier.verify(AuthProvider.GOOGLE, "provider-jwt")).thenReturn(userInfo);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account a = invocation.getArgument(0);
            if (a.getId() == null) {
                return Account.reconstruct(
                        88L,
                        a.getUuid(),
                        null,
                        a.getCreatedAt(),
                        null,
                        null,
                        a.getEmail(),
                        a.getPhoneNumber(),
                        a.getPasswordHash(),
                        a.getStatus(),
                        a.getEmailVerified(),
                        a.getPhoneVerified(),
                        a.getTwoFactorEnabled(),
                        a.getLastLoginAt(),
                        a.getLastLoginIp()
                );
            }
            return a;
        });
        when(accountDeviceRepository.findByAccountIdAndFingerprint(88L, IdentityUseCaseTestFixtures.FINGERPRINT))
                .thenReturn(Optional.empty());
        when(accountDeviceRepository.save(any(AccountDevice.class))).thenAnswer(inv -> {
            AccountDevice d = inv.getArgument(0);
            return AccountDevice.reconstruct(
                    60L,
                    d.getUuid(),
                    null,
                    d.getCreatedAt(),
                    null,
                    null,
                    d.getAccountId(),
                    d.getDeviceType(),
                    d.getDeviceName(),
                    d.getFingerprint(),
                    d.getUserAgent(),
                    d.getPushToken(),
                    d.getLastActiveAt(),
                    d.getIsTrusted()
            );
        });
        when(accountSessionTokenService.openSessionAndIssueTokens(any(), eq(60L), eq(IdentityUseCaseTestFixtures.IP)))
                .thenAnswer(inv -> {
                    Account acc = inv.getArgument(0);
                    return new AuthTokenResponse("oa", "or", 3600, acc.getUuid());
                });

        var response = useCase.execute(request(), IdentityUseCaseTestFixtures.IP);

        assertThat(response.isNewAccount()).isTrue();
        assertThat(response.accessToken()).isEqualTo("oa");
        assertThat(response.refreshToken()).isEqualTo("or");
        verify(accountCredentialRepository).save(any());
    }

    @Test
    @DisplayName("Existing inactive account → ACCOUNT_NOT_ACTIVE")
    void executeWhenExistingInactiveThrowsAccountNotActive() {
        var userInfo = new OAuthProviderVerifier.OAuthUserInfo(IdentityUseCaseTestFixtures.EMAIL, "sub-1");
        var pending = IdentityUseCaseTestFixtures.pendingAccount(5L);
        when(oAuthProviderVerifier.verify(AuthProvider.GOOGLE, "provider-jwt")).thenReturn(userInfo);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(request(), IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Existing active + provider linked → tokens (not new account)")
    void executeWhenExistingActiveReusesAccountAndReturnsTokens() {
        var userInfo = new OAuthProviderVerifier.OAuthUserInfo(IdentityUseCaseTestFixtures.EMAIL, "sub-1");
        var account = IdentityUseCaseTestFixtures.activeAccount(7L);
        var device = IdentityUseCaseTestFixtures.deviceRow(70L, 7L, IdentityUseCaseTestFixtures.FINGERPRINT, true);

        when(oAuthProviderVerifier.verify(AuthProvider.GOOGLE, "provider-jwt")).thenReturn(userInfo);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(accountCredentialRepository.existsByAccountIdAndProvider(7L, AuthProvider.GOOGLE)).thenReturn(true);
        when(accountDeviceRepository.findByAccountIdAndFingerprint(7L, IdentityUseCaseTestFixtures.FINGERPRINT))
                .thenReturn(Optional.of(device));
        when(accountDeviceRepository.save(any(AccountDevice.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountSessionTokenService.openSessionAndIssueTokens(eq(account), eq(70L), eq(IdentityUseCaseTestFixtures.IP)))
                .thenReturn(new AuthTokenResponse("a", "r", 3600, account.getUuid()));

        var response = useCase.execute(request(), IdentityUseCaseTestFixtures.IP);

        assertThat(response.isNewAccount()).isFalse();
        assertThat(response.accountUuid()).isEqualTo(account.getUuid());
    }
}
