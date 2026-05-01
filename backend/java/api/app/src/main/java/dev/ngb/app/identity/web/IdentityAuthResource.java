package dev.ngb.app.identity.web;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.CreateSessionUseCase;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.CreateOAuthSessionUseCase;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.CompleteSessionVerificationUseCase;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.CompleteSessionVerificationRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.CreatePasswordResetUseCase;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetResponse;
import dev.ngb.app.identity.application.usecase.password.reset_password.CompletePasswordResetUseCase;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.CreateAccountUseCase;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.CreateEmailVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationResponse;
import dev.ngb.app.identity.application.usecase.registration.verify_email.CompleteEmailVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.DeleteCurrentSessionUseCase;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.DeleteCurrentSessionRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.CreateTokenUseCase;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.CreateTokenRequest;
import dev.ngb.application.port.config.SecurityJwtConfig;
import dev.ngb.infrastructure.web.RequestUtils;
import dev.ngb.infrastructure.web.ResourceResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IdentityAuthResource implements IdentityAuthEndpoint {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final CreateAccountUseCase createAccountUseCase;
    private final CompleteEmailVerificationUseCase completeEmailVerificationUseCase;
    private final CreateEmailVerificationUseCase createEmailVerificationUseCase;
    private final CreateSessionUseCase createSessionUseCase;
    private final CompleteSessionVerificationUseCase completeSessionVerificationUseCase;
    private final CreateOAuthSessionUseCase createOAuthSessionUseCase;
    private final CreateTokenUseCase createTokenUseCase;
    private final DeleteCurrentSessionUseCase deleteCurrentSessionUseCase;
    private final CreatePasswordResetUseCase createPasswordResetUseCase;
    private final CompletePasswordResetUseCase completePasswordResetUseCase;
    private final SecurityJwtConfig securityJwtConfig;

    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest request) {
        CreateAccountResponse response = createAccountUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> completeEmailVerification(String verificationId,
                                                         CompleteEmailVerificationRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        AuthTokenResponse response = completeEmailVerificationUseCase.execute(verificationId, request, ipAddress);
        return responseWithRefreshTokenCookie(response, httpRequest);
    }

    @Override
    public ResponseEntity<CreateEmailVerificationResponse> createEmailVerification(CreateEmailVerificationRequest request) {
        CreateEmailVerificationResponse response = createEmailVerificationUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<CreateSessionResponse> createSession(CreateSessionRequest request,
                                                      HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        CreateSessionResponse response = createSessionUseCase.execute(request, ipAddress);
        return responseWithRefreshTokenCookie(response, httpRequest);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> completeSessionVerification(CompleteSessionVerificationRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        AuthTokenResponse response = completeSessionVerificationUseCase.execute(request, ipAddress);
        return responseWithRefreshTokenCookie(response, httpRequest);
    }

    @Override
    public ResponseEntity<CreateOAuthSessionResponse> createOAuthSession(CreateOAuthSessionRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        CreateOAuthSessionResponse response = createOAuthSessionUseCase.execute(request, ipAddress);
        return responseWithRefreshTokenCookie(response, httpRequest);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> createToken(CreateTokenRequest request, HttpServletRequest httpRequest) {
        String refreshToken = extractRefreshToken(request == null ? null : request.refreshToken(), httpRequest);
        AuthTokenResponse response = createTokenUseCase.execute(new CreateTokenRequest(refreshToken));
        return responseWithRefreshTokenCookie(response, httpRequest);
    }

    @Override
    public ResponseEntity<Void> deleteCurrentSession(DeleteCurrentSessionRequest request, HttpServletRequest httpRequest) {
        String refreshToken = extractRefreshToken(request == null ? null : request.refreshToken(), httpRequest);
        deleteCurrentSessionUseCase.execute(new DeleteCurrentSessionRequest(refreshToken));
        return ResourceResponse.noContentWithCookie(buildClearRefreshTokenCookie(httpRequest));
    }

    @Override
    public ResponseEntity<CreatePasswordResetResponse> createPasswordReset(CreatePasswordResetRequest request) {
        CreatePasswordResetResponse response = createPasswordResetUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<Void> completePasswordReset(String resetId, CompletePasswordResetRequest request) {
        completePasswordResetUseCase.execute(resetId, request);
        return ResourceResponse.noContent();
    }

    private ResponseEntity<AuthTokenResponse> responseWithRefreshTokenCookie(AuthTokenResponse response, HttpServletRequest httpRequest) {
        if (response.refreshToken() == null) {
            return ResourceResponse.ok(response);
        }

        AuthTokenResponse body = new AuthTokenResponse(
                response.accessToken(),
                null,
                response.expiresIn(),
                response.accountUuid()
        );

        return ResourceResponse.okWithCookie(body, buildRefreshTokenCookie(response.refreshToken(), httpRequest));
    }

    private ResponseEntity<CreateSessionResponse> responseWithRefreshTokenCookie(CreateSessionResponse response, HttpServletRequest httpRequest) {
        if (response.refreshToken() == null) {
            return ResourceResponse.ok(response);
        }

        CreateSessionResponse body = CreateSessionResponse.authenticated(
                response.accessToken(),
                null,
                response.expiresIn(),
                response.accountUuid()
        );
        return ResourceResponse.okWithCookie(body, buildRefreshTokenCookie(response.refreshToken(), httpRequest));
    }

    private ResponseEntity<CreateOAuthSessionResponse> responseWithRefreshTokenCookie(CreateOAuthSessionResponse response, HttpServletRequest httpRequest) {
        if (response.refreshToken() == null) {
            return ResourceResponse.ok(response);
        }

        CreateOAuthSessionResponse body = new CreateOAuthSessionResponse(
                response.accessToken(),
                null,
                response.expiresIn(),
                response.accountUuid(),
                response.isNewAccount()
        );
        return ResourceResponse.okWithCookie(body, buildRefreshTokenCookie(response.refreshToken(), httpRequest));
    }

    private String extractRefreshToken(String refreshTokenFromRequest, HttpServletRequest httpRequest) {
        String normalizedRequestToken = normalizeToken(refreshTokenFromRequest);
        if (normalizedRequestToken != null) {
            return normalizedRequestToken;
        }
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (IdentityAuthResource.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return normalizeToken(cookie.getValue());
            }
        }
        return null;
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return null;
        }
        String normalized = token.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken, HttpServletRequest httpRequest) {
        return ResourceResponse.buildHttpOnlyCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                "/api/app/",
                securityJwtConfig.securityJwtRefreshTokenExpiry(),
                httpRequest
        );
    }

    private ResponseCookie buildClearRefreshTokenCookie(HttpServletRequest httpRequest) {
        return ResourceResponse.buildClearHttpOnlyCookie(
                REFRESH_TOKEN_COOKIE_NAME,
                "/api/app/",
                httpRequest
        );
    }
}
