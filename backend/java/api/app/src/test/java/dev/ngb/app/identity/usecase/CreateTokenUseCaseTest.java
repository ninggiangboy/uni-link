package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.usecase.session.refresh_token.CreateTokenUseCase;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.CreateTokenRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import dev.ngb.domain.identity.service.SessionRotationDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTokenUseCase")
class CreateTokenUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountSessionRepository accountSessionRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Spy
    private SessionRotationDomainService sessionRotationDomainService;

    @InjectMocks
    private CreateTokenUseCase useCase;

    private final CreateTokenRequest request = new CreateTokenRequest("raw-refresh");

    @Test
    @DisplayName("No session for token hash → INVALID_REFRESH_TOKEN")
    void executeWhenSessionMissingThrowsInvalidRefresh() {
        when(tokenProvider.hashToken("raw-refresh")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Revoked / invalid session → INVALID_REFRESH_TOKEN")
    void executeWhenSessionInvalidThrowsInvalidRefresh() {
        var session = AccountSession.reconstruct(
                1L,
                "s",
                null,
                Instant.now(),
                null,
                Instant.now(),
                10L,
                20L,
                "h1",
                IdentityUseCaseTestFixtures.IP,
                Instant.now().plus(1, ChronoUnit.DAYS),
                true
        );
        when(tokenProvider.hashToken("raw-refresh")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.of(session));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Account missing → ACCOUNT_NOT_FOUND")
    void executeWhenAccountMissingThrowsNotFound() {
        var session = IdentityUseCaseTestFixtures.validSession(1L, 10L, 20L, "h1");
        when(tokenProvider.hashToken("raw-refresh")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.of(session));
        when(accountRepository.findById(10L)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Account not active → ACCOUNT_NOT_ACTIVE")
    void executeWhenAccountNotActiveThrows() {
        var session = IdentityUseCaseTestFixtures.validSession(1L, 10L, 20L, "h1");
        var pending = IdentityUseCaseTestFixtures.pendingAccount(10L);
        when(tokenProvider.hashToken("raw-refresh")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.of(session));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(pending));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Valid refresh → rotate session + new tokens")
    void executeWhenValidRotatesSessionAndReturnsNewTokens() {
        var session = IdentityUseCaseTestFixtures.validSession(1L, 10L, 20L, "h1");
        var account = IdentityUseCaseTestFixtures.activeAccount(10L);
        when(tokenProvider.hashToken("raw-refresh")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.of(session));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(tokenProvider.generateRefreshToken()).thenReturn("new-raw");
        when(tokenProvider.hashToken("new-raw")).thenReturn("h2");
        when(tokenProvider.generateAccessToken(10L, account.getUuid(), IdentityUseCaseTestFixtures.EMAIL)).thenReturn("new-access");
        when(tokenProvider.getAccessTokenExpiresInSeconds()).thenReturn(1800L);

        var response = useCase.execute(request);

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-raw");
        verify(accountSessionRepository, times(2)).save(any(AccountSession.class));
    }
}
