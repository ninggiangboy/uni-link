package dev.ngb.app.identity.application.service;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.application.ApplicationService;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import dev.ngb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Opens a new session and returns access + refresh tokens. Shared by every path that completes
 * authentication with a known account and device (verify email, trusted password login, OTP login
 * completion, OAuth). RefreshTokenUseCase intentionally stays separate — it rotates instead.
 */
@Slf4j
@RequiredArgsConstructor
public class AccountSessionTokenService implements ApplicationService {

    private final AccountSessionRepository accountSessionRepository;
    private final TokenProvider tokenProvider;

    public AuthTokenResponse openSessionAndIssueTokens(Account account, Long deviceId, String ipAddress) {
        log.debug(
                "Open session start accountId={}, accountUuid={}, deviceId={}, email={}, ip={}",
                account.getId(),
                account.getUuid(),
                deviceId,
                StringUtils.maskEmail(account.getEmail()),
                ipAddress
        );

        String refreshToken = tokenProvider.generateRefreshToken();
        String refreshHash = tokenProvider.hashToken(refreshToken);
        AccountSession session = AccountSession.create(
                account.getId(),
                deviceId,
                refreshHash,
                ipAddress
        );
        AccountSession savedSession = accountSessionRepository.save(session);
        log.debug(
                "Session persisted accountId={}, deviceId={}, sessionId={}",
                account.getId(),
                deviceId,
                savedSession.getId()
        );

        long accessTtlSeconds = tokenProvider.getAccessTokenExpiresInSeconds();
        String accessToken = tokenProvider.generateAccessToken(
                account.getId(),
                account.getUuid(),
                account.getEmail()
        );
        log.debug(
                "Access JWT issued accountId={}, sessionId={}, accessTtlSeconds={} (tokens not logged)",
                account.getId(),
                savedSession.getId(),
                accessTtlSeconds
        );

        log.info(
                "Session opened accountId={}, accountUuid={}, deviceId={}, sessionId={}, accessTtlSeconds={}",
                account.getId(),
                account.getUuid(),
                deviceId,
                savedSession.getId(),
                accessTtlSeconds
        );

        return new AuthTokenResponse(
                accessToken,
                refreshToken,
                accessTtlSeconds,
                account.getUuid()
        );
    }
}
