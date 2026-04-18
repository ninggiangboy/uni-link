package dev.ngb.app.identity.application.usecase.session.refresh_token;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.RefreshTokenRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Refreshes the access token using a valid refresh token with rotation. The presented refresh is
 * hashed and matched to an AccountSession; missing or unknown hashes yield INVALID_REFRESH_TOKEN.
 * The session must still be valid (not revoked or expired per domain rules), and the account must
 * remain active.
 *
 * The old session is revoked and saved so the previous refresh cannot be reused. A new session
 * reuses the same device and IP for auditing, stores the hash of a newly issued refresh token,
 * and a fresh access JWT is returned in AuthTokenResponse.
 */
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenUseCase implements UseCaseService {

    private final AccountRepository accountRepository;
    private final AccountSessionRepository accountSessionRepository;
    private final TokenProvider tokenProvider;

    public AuthTokenResponse execute(RefreshTokenRequest request) {
        log.debug("Refresh token attempt");

        // Lookup is always by hash so a DB leak never exposes usable refresh tokens.
        String tokenHash = tokenProvider.hashToken(request.refreshToken());

        AccountSession session = accountSessionRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> {
                    log.warn("Refresh token failed: session not found or invalid");
                    return AccountError.INVALID_REFRESH_TOKEN.exception();
                });

        // Revoked or expired rows reuse the same error as unknown hash.
        if (!session.isValid()) {
            log.warn("Refresh token failed: session revoked or expired sessionId={}", session.getId());
            throw AccountError.INVALID_REFRESH_TOKEN.exception();
        }

        // Session can outlive a deleted account in theory; treat as invalid refresh.
        Account account = accountRepository.findById(session.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Refresh token failed: account not found accountId={}", session.getAccountId());
                    return AccountError.ACCOUNT_NOT_FOUND.exception();
                });

        if (!account.isActive()) {
            log.warn("Refresh token failed: account not active accountId={}", account.getId());
            throw AccountError.ACCOUNT_NOT_ACTIVE.exception();
        }

        // Rotation: old refresh must fail on the next call even if stolen.
        session.revoke();
        accountSessionRepository.save(session);

        String newRefreshToken = tokenProvider.generateRefreshToken();
        // Preserve device binding and last known IP for continuity and audit.
        AccountSession newSession = AccountSession.create(
                account.getId(), session.getDeviceId(),
                tokenProvider.hashToken(newRefreshToken), session.getIpAddress()
        );
        accountSessionRepository.save(newSession);

        String accessToken = tokenProvider.generateAccessToken(
                account.getId(), account.getUuid(), account.getEmail()
        );

        log.info("Refresh token successful accountId={}, accountUuid={}", account.getId(), account.getUuid());
        return new AuthTokenResponse(
                accessToken, newRefreshToken,
                tokenProvider.getAccessTokenExpiresInSeconds(),
                account.getUuid()
        );
    }
}
