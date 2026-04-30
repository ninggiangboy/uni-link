package dev.ngb.app.identity.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.VerifyLoginRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.ForgotPasswordRequest;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.ResetPasswordRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.ResendVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.VerifyEmailRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.LogoutAccountRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.RefreshTokenRequest;
import dev.ngb.app.support.NoContent;
import dev.ngb.app.support.RequestJsonClient;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.springframework.web.client.RestTemplate;

/**
 * Composable HTTP client for {@code /api/auth/*}. {@code Left} = {@link ErrorResponse};
 * {@code Right} = success DTO or {@link NoContent} for empty bodies.
 */
public final class IdentityAuthTestClient {

    private static final String AUTH_ENDPOINT = "/api/auth";
    private static final String REGISTER_ENDPOINT = AUTH_ENDPOINT + "/register";
    private static final String VERIFY_EMAIL_ENDPOINT = AUTH_ENDPOINT + "/verify-email";
    private static final String VERIFY_EMAIL_RESEND_ENDPOINT = AUTH_ENDPOINT + "/verify-email/resend";
    private static final String LOGIN_ENDPOINT = AUTH_ENDPOINT + "/login";
    private static final String LOGIN_VERIFY_ENDPOINT = AUTH_ENDPOINT + "/login/verify";
    private static final String TOKEN_REFRESH_ENDPOINT = AUTH_ENDPOINT + "/token/refresh";
    private static final String LOGOUT_ENDPOINT = AUTH_ENDPOINT + "/logout";
    private static final String OAUTH_ENDPOINT = AUTH_ENDPOINT + "/oauth";
    private static final String FORGOT_PASSWORD_ENDPOINT = AUTH_ENDPOINT + "/forgot-password";
    private static final String RESET_PASSWORD_ENDPOINT = AUTH_ENDPOINT + "/reset-password";

    private final RequestJsonClient json;

    public IdentityAuthTestClient(ObjectMapper objectMapper, RestTemplate restTemplate, String baseUrl) {
        this.json = new RequestJsonClient(objectMapper, baseUrl, restTemplate);
    }

    public Either<ErrorResponse, RegisterAccountResponse> registerAccount(RegisterAccountRequest request) {
        return json.post(REGISTER_ENDPOINT, request, RegisterAccountResponse.class);
    }

    public Either<ErrorResponse, AuthTokenResponse> verifyEmail(VerifyEmailRequest request) {
        return json.post(VERIFY_EMAIL_ENDPOINT, request, AuthTokenResponse.class);
    }

    public Either<ErrorResponse, LoginAccountResponse> login(LoginAccountRequest request) {
        return json.post(LOGIN_ENDPOINT, request, LoginAccountResponse.class);
    }

    public Either<ErrorResponse, AuthTokenResponse> verifyLogin(VerifyLoginRequest request) {
        return json.post(LOGIN_VERIFY_ENDPOINT, request, AuthTokenResponse.class);
    }

    public Either<ErrorResponse, AuthTokenResponse> refreshToken(RefreshTokenRequest request) {
        return json.post(TOKEN_REFRESH_ENDPOINT, request, AuthTokenResponse.class);
    }

    public Either<ErrorResponse, NoContent> resendVerification(ResendVerificationRequest request) {
        return json.post(VERIFY_EMAIL_RESEND_ENDPOINT, request, NoContent.class);
    }

    public Either<ErrorResponse, NoContent> logout(LogoutAccountRequest request) {
        return json.post(LOGOUT_ENDPOINT, request, NoContent.class);
    }

    public Either<ErrorResponse, NoContent> forgotPassword(ForgotPasswordRequest request) {
        return json.post(FORGOT_PASSWORD_ENDPOINT, request, NoContent.class);
    }

    public Either<ErrorResponse, NoContent> resetPassword(ResetPasswordRequest request) {
        return json.post(RESET_PASSWORD_ENDPOINT, request, NoContent.class);
    }

    public Either<ErrorResponse, OAuthLoginResponse> oauthLogin(OAuthLoginRequest request) {
        return json.post(OAUTH_ENDPOINT, request, OAuthLoginResponse.class);
    }
}
