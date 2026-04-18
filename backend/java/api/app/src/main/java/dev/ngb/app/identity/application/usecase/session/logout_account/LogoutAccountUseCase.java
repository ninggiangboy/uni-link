package dev.ngb.app.identity.application.usecase.session.logout_account;

import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.LogoutAccountRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Logs out the session tied to the refresh token the client still holds. When a matching
 * AccountSession exists, it is revoked and persisted so that refresh token stops working. Short-lived
 * access JWTs may remain valid until they expire naturally.
 *
 * If no refresh token is supplied, the call is a no-op so clients do not hard-fail on optional
 * logout. If the hash does not match any session, a debug log is enough—it may already be logged
 * out or the token may be invalid.
 */
@Slf4j
@RequiredArgsConstructor
public class LogoutAccountUseCase implements UseCaseService {

    private final AccountSessionRepository accountSessionRepository;
    private final TokenProvider tokenProvider;

    public void execute(LogoutAccountRequest request) {
        log.debug("Logout attempt");

        // Optional body: mobile clients sometimes call logout after local token wipe.
        if (request.refreshToken() == null) {
            log.debug("Logout: no refresh token provided, skipping");
            return;
        }

        String tokenHash = tokenProvider.hashToken(request.refreshToken());
        accountSessionRepository.findByTokenHash(tokenHash)
                .ifPresentOrElse(
                        session -> {
                            // Single session row: other devices stay signed in.
                            session.revoke();
                            accountSessionRepository.save(session);
                            log.info("Logout successful accountId={}, sessionId={}", session.getAccountId(), session.getId());
                        },
                        () -> log.debug("Logout: no session found for token (already invalid or expired)")
                );
    }
}
