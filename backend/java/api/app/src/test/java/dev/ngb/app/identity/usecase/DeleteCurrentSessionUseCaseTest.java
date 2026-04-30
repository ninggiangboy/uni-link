package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.usecase.session.logout_account.DeleteCurrentSessionUseCase;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.DeleteCurrentSessionRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCurrentSessionUseCase")
class DeleteCurrentSessionUseCaseTest {

    @Mock
    private AccountSessionRepository accountSessionRepository;
    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private DeleteCurrentSessionUseCase useCase;

    @Test
    @DisplayName("Null refresh token → no repository lookup")
    void executeWhenRefreshTokenNullSkipsRepository() {
        useCase.execute(new DeleteCurrentSessionRequest(null));

        verify(accountSessionRepository, never()).findByTokenHash(any());
    }

    @Test
    @DisplayName("Unknown session hash → does not save")
    void executeWhenSessionUnknownDoesNotSave() {
        when(tokenProvider.hashToken("tok")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.empty());

        useCase.execute(new DeleteCurrentSessionRequest("tok"));

        verify(accountSessionRepository, never()).save(any(AccountSession.class));
    }

    @Test
    @DisplayName("Session found → revoke and persist")
    void executeWhenSessionFoundRevokesAndSaves() {
        var session = IdentityUseCaseTestFixtures.validSession(5L, 10L, 20L, "h1");
        when(tokenProvider.hashToken("tok")).thenReturn("h1");
        when(accountSessionRepository.findByTokenHash("h1")).thenReturn(Optional.of(session));

        useCase.execute(new DeleteCurrentSessionRequest("tok"));

        verify(accountSessionRepository).save(session);
    }
}
