package dev.ngb.app.identity.support;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.CompleteSessionVerificationRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetResponse;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationResponse;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.DeleteCurrentSessionRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.CreateTokenRequest;
import dev.ngb.app.support.EmptyBody;
import dev.ngb.app.support.HttpJsonClient;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;

/**
 * Composable HTTP client for identity REST endpoints. {@code Left} = {@link ErrorResponse};
 * {@code Right} = success DTO or {@link EmptyBody} for empty bodies.
 */
public final class IdentityAuthApiClient {

    private static final String IDENTITY_ENDPOINT = "/identity";
    private static final String ACCOUNTS_ENDPOINT = IDENTITY_ENDPOINT + "/accounts";
    private static final String EMAIL_VERIFICATIONS_ENDPOINT = IDENTITY_ENDPOINT + "/email-verifications";
    private static final String SESSIONS_ENDPOINT = IDENTITY_ENDPOINT + "/sessions";
    private static final String SESSION_VERIFICATION_ENDPOINT = IDENTITY_ENDPOINT + "/sessions/verification";
    private static final String TOKENS_ENDPOINT = IDENTITY_ENDPOINT + "/tokens";
    private static final String CURRENT_SESSION_ENDPOINT = IDENTITY_ENDPOINT + "/sessions/current";
    private static final String OAUTH_SESSIONS_ENDPOINT = IDENTITY_ENDPOINT + "/sessions/oauth";
    private static final String PASSWORD_RESETS_ENDPOINT = IDENTITY_ENDPOINT + "/password-resets";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final HttpJsonClient json;

    public IdentityAuthApiClient(HttpJsonClient json) {
        this.json = json;
    }

    public Either<ErrorResponse, CreateAccountResponse> createAccount(CreateAccountRequest request) {
        return json.post(ACCOUNTS_ENDPOINT, request, CreateAccountResponse.class);
    }

    public Either<ErrorResponse, AuthTokenResponse> completeEmailVerification(String verificationId, CompleteEmailVerificationRequest request) {
        return json.patch(EMAIL_VERIFICATIONS_ENDPOINT + "/" + verificationId, request, AuthTokenResponse.class);
    }

    public Either<ErrorResponse, CreateSessionResponse> createSession(CreateSessionRequest request) {
        return json.post(SESSIONS_ENDPOINT, request, CreateSessionResponse.class);
    }

    public Either<ErrorResponse, AuthTokenResponse> completeSessionVerification(CompleteSessionVerificationRequest request) {
        return json.post(SESSION_VERIFICATION_ENDPOINT, request, AuthTokenResponse.class);
    }

    public Either<ErrorResponse, AuthTokenResponse> createToken(CreateTokenRequest request) {
        return json.post(TOKENS_ENDPOINT, request, AuthTokenResponse.class);
    }

    public Either<ErrorResponse, CreateEmailVerificationResponse> createEmailVerification(CreateEmailVerificationRequest request) {
        return json.post(EMAIL_VERIFICATIONS_ENDPOINT, request, CreateEmailVerificationResponse.class);
    }

    public Either<ErrorResponse, EmptyBody> deleteCurrentSession(DeleteCurrentSessionRequest request) {
        return json.delete(CURRENT_SESSION_ENDPOINT, request, EmptyBody.class);
    }

    public Either<ErrorResponse, CreatePasswordResetResponse> createPasswordReset(CreatePasswordResetRequest request) {
        return json.post(PASSWORD_RESETS_ENDPOINT, request, CreatePasswordResetResponse.class);
    }

    public Either<ErrorResponse, EmptyBody> completePasswordReset(String resetId, CompletePasswordResetRequest request) {
        return json.patch(PASSWORD_RESETS_ENDPOINT + "/" + resetId, request, EmptyBody.class);
    }

    public Either<ErrorResponse, CreateOAuthSessionResponse> createOAuthSession(CreateOAuthSessionRequest request) {
        return json.post(OAUTH_SESSIONS_ENDPOINT, request, CreateOAuthSessionResponse.class);
    }

    public String refreshTokenCookie() {
        return json.getCookie(REFRESH_TOKEN_COOKIE_NAME);
    }
}
