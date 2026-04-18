package dev.ngb.app.identity.integration;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.dto.DeviceInfo;
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
import dev.ngb.app.identity.support.TestOtpSender;
import dev.ngb.app.support.AbstractIntegrationTest;
import dev.ngb.app.support.TestUtils;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.web.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HTTP integration tests for {@code /api/auth/*}, ordered around the flows documented under
 * {@code docs/auth/} (overview, registration-flow, login-flow, token-lifecycle, oauth-flow,
 * password-reset-flow). Uses {@link AbstractIntegrationTest}, {@link TestUtils},
 * and {@link TestOtpSender}.
 */
@DisplayName("Auth API")
class IdentityAuthIntegrationTest extends AbstractIntegrationTest {

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

    @Autowired
    private TestOtpSender testOtpSender;

    @BeforeEach
    void clearCapturedOtps() {
        testOtpSender.clear();
    }

    @Test
    @DisplayName("Registration: duplicate email → 409 EMAIL_ALREADY_EXISTS")
    void registrationRegisterThenDuplicateEmailReturnsConflict() {
        String email = TestUtils.getUniqueEmail();
        RegisterAccountRequest body = new RegisterAccountRequest(email, "Password1!");

        ResponseEntity<RegisterAccountResponse> first = postJson(REGISTER_ENDPOINT, body, RegisterAccountResponse.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(first.getBody()).isNotNull();
        assertThat(first.getBody().accountUuid()).isNotBlank();

        ResponseEntity<ErrorResponse> second = postJson(REGISTER_ENDPOINT, body, ErrorResponse.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(second.getBody()).isNotNull();
        assertThat(second.getBody().error()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("Registration: register → resend OTP → verify-email")
    void registrationRegisterResendThenVerifyEmail() {
        String email = TestUtils.getUniqueEmail();
        String password = "Password1!";
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-reg-resend");

        assertThat(postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, password), RegisterAccountResponse.class)
                .getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);

        ResponseEntity<Void> resend = postJson(VERIFY_EMAIL_RESEND_ENDPOINT,
                new ResendVerificationRequest(email),
                Void.class
        );
        assertThat(resend.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);

        String otpAfterResend = testOtpSender.getLastOtpCode().orElseThrow();
        ResponseEntity<AuthTokenResponse> verifyResp = postJson(
                VERIFY_EMAIL_ENDPOINT,
                new VerifyEmailRequest(email, otpAfterResend, device),
                AuthTokenResponse.class
        );
        assertThat(verifyResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verifyResp.getBody()).isNotNull();
        assertThat(verifyResp.getBody().accessToken()).isNotBlank();
        assertThat(verifyResp.getBody().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Registration: verify-email when already verified → 409")
    void registrationVerifyEmailWhenAlreadyVerifiedReturnsConflict() {
        String email = TestUtils.getUniqueEmail();
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-v1");

        postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, "Password1!"), RegisterAccountResponse.class);
        String otp = testOtpSender.getLastOtpCode().orElseThrow();
        postJson(VERIFY_EMAIL_ENDPOINT, new VerifyEmailRequest(email, otp, device), AuthTokenResponse.class);

        ResponseEntity<ErrorResponse> again = postJson(VERIFY_EMAIL_ENDPOINT,
                new VerifyEmailRequest(email, otp, device),
                ErrorResponse.class
        );
        assertThat(again.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(again.getBody()).isNotNull();
        assertThat(again.getBody().error()).isEqualTo("EMAIL_ALREADY_VERIFIED");
    }

    @Test
    @DisplayName("Registration: resend while pending → 204 + REGISTRATION OTP")
    void registrationResendVerificationWhenPendingSucceeds() {
        String email = TestUtils.getUniqueEmail();
        postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, "Password1!"), RegisterAccountResponse.class);

        ResponseEntity<Void> resend = postJson(VERIFY_EMAIL_RESEND_ENDPOINT,
                new ResendVerificationRequest(email),
                Void.class
        );
        assertThat(resend.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(testOtpSender.getLastSent()).isPresent();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);
    }

    @Test
    @DisplayName("Login: wrong password → 401 INVALID_CREDENTIALS")
    void loginWithWrongPasswordReturnsUnauthorized() {
        String email = TestUtils.getUniqueEmail();
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-wp");

        postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, "Password1!"), RegisterAccountResponse.class);
        String otp = testOtpSender.getLastOtpCode().orElseThrow();
        postJson(VERIFY_EMAIL_ENDPOINT, new VerifyEmailRequest(email, otp, device), AuthTokenResponse.class);

        ResponseEntity<ErrorResponse> bad = postJson(
                LOGIN_ENDPOINT,
                new LoginAccountRequest(email, "wrong", device),
                ErrorResponse.class
        );
        assertThat(bad.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(bad.getBody()).isNotNull();
        assertThat(bad.getBody().error()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("Login: new device → OTP → POST /login/verify → tokens")
    void loginNewDeviceRequiresOtpThenVerifyLoginIssuesTokens() {
        String email = TestUtils.getUniqueEmail();
        String password = "Password1!";
        DeviceInfo firstDevice = new DeviceInfo(DeviceType.WEB, "d1", "fp-first");
        DeviceInfo secondDevice = new DeviceInfo(DeviceType.WEB, "d2", "fp-second");

        postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, password), RegisterAccountResponse.class);
        String regOtp = testOtpSender.getLastOtpCode().orElseThrow();
        postJson(VERIFY_EMAIL_ENDPOINT, new VerifyEmailRequest(email, regOtp, firstDevice), AuthTokenResponse.class);

        ResponseEntity<LoginAccountResponse> stepUp = postJson(
                LOGIN_ENDPOINT,
                new LoginAccountRequest(email, password, secondDevice),
                LoginAccountResponse.class
        );
        assertThat(stepUp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(stepUp.getBody()).isNotNull();
        assertThat(stepUp.getBody().requiresVerification()).isTrue();
        assertThat(stepUp.getBody().verificationToken()).isNotBlank();

        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.LOGIN);
        String loginOtp = testOtpSender.getLastOtpCode().orElseThrow();

        ResponseEntity<AuthTokenResponse> done = postJson(
                LOGIN_VERIFY_ENDPOINT,
                new VerifyLoginRequest(stepUp.getBody().verificationToken(), loginOtp),
                AuthTokenResponse.class
        );
        assertThat(done.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(done.getBody()).isNotNull();
        assertThat(done.getBody().accessToken()).isNotBlank();
        assertThat(done.getBody().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Token lifecycle: login → refresh → logout → refresh → 401")
    void tokenRefreshLogoutThenStaleRefreshReturnsUnauthorized() {
        String email = TestUtils.getUniqueEmail();
        String password = "Password1!";
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "integration-browser", "fp-known-1");

        assertThat(postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, password), RegisterAccountResponse.class)
                .getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(testOtpSender.getLastSent()).isPresent();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);
        String regOtp = testOtpSender.getLastOtpCode().orElseThrow();

        ResponseEntity<AuthTokenResponse> verifyResp = postJson(
                VERIFY_EMAIL_ENDPOINT,
                new VerifyEmailRequest(email, regOtp, device),
                AuthTokenResponse.class
        );
        assertThat(verifyResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(verifyResp.getBody()).isNotNull();
        assertThat(verifyResp.getBody().accessToken()).isNotBlank();
        assertThat(verifyResp.getBody().refreshToken()).isNotBlank();

        ResponseEntity<LoginAccountResponse> loginResp = postJson(
                LOGIN_ENDPOINT,
                new LoginAccountRequest(email, password, device),
                LoginAccountResponse.class
        );
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).isNotNull();
        assertThat(loginResp.getBody().requiresVerification()).isFalse();
        assertThat(loginResp.getBody().accessToken()).isNotBlank();
        String refresh = loginResp.getBody().refreshToken();

        ResponseEntity<AuthTokenResponse> refreshResp = postJson(
                TOKEN_REFRESH_ENDPOINT,
                new RefreshTokenRequest(refresh),
                AuthTokenResponse.class
        );
        assertThat(refreshResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refreshResp.getBody()).isNotNull();
        String newRefresh = refreshResp.getBody().refreshToken();

        ResponseEntity<Void> logoutResp = postJson(
                LOGOUT_ENDPOINT,
                new LogoutAccountRequest(newRefresh),
                Void.class
        );
        assertThat(logoutResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> staleRefresh = postJson(
                TOKEN_REFRESH_ENDPOINT,
                new RefreshTokenRequest(newRefresh),
                ErrorResponse.class
        );
        assertThat(staleRefresh.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(staleRefresh.getBody()).isNotNull();
        assertThat(staleRefresh.getBody().error()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    @DisplayName("OAuth: POST /oauth valid provider token → new account + tokens")
    void oauthLoginWithIntegrationTokenCreatesAccountAndReturnsTokens() {
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "oauth-client", "fp-oauth-1");
        OAuthLoginRequest body = new OAuthLoginRequest(AuthProvider.GOOGLE, "integration-oauth-valid", device);

        ResponseEntity<OAuthLoginResponse> resp = postJson(OAUTH_ENDPOINT, body, OAuthLoginResponse.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().isNewAccount()).isTrue();
        assertThat(resp.getBody().accessToken()).isNotBlank();
        assertThat(resp.getBody().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Password reset: forgot → reset → old password fails, new works")
    void passwordForgotThenResetAllowsLoginWithNewPassword() {
        String email = TestUtils.getUniqueEmail();
        String oldPassword = "Password1!";
        String newPassword = "Password2!";
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-reset");

        postJson(REGISTER_ENDPOINT, new RegisterAccountRequest(email, oldPassword), RegisterAccountResponse.class);
        String regOtp = testOtpSender.getLastOtpCode().orElseThrow();
        postJson(VERIFY_EMAIL_ENDPOINT, new VerifyEmailRequest(email, regOtp, device), AuthTokenResponse.class);

        assertThat(postJson(FORGOT_PASSWORD_ENDPOINT, new ForgotPasswordRequest(email), Void.class).getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.PASSWORD_RESET);
        String resetOtp = testOtpSender.getLastOtpCode().orElseThrow();

        assertThat(postJson(
                RESET_PASSWORD_ENDPOINT,
                new ResetPasswordRequest(email, resetOtp, newPassword),
                Void.class
        ).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<ErrorResponse> oldPwFails = postJson(
                LOGIN_ENDPOINT,
                new LoginAccountRequest(email, oldPassword, device),
                ErrorResponse.class
        );
        assertThat(oldPwFails.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(oldPwFails.getBody()).isNotNull();
        assertThat(oldPwFails.getBody().error()).isEqualTo("INVALID_CREDENTIALS");

        ResponseEntity<LoginAccountResponse> newPwOk = postJson(
                LOGIN_ENDPOINT,
                new LoginAccountRequest(email, newPassword, device),
                LoginAccountResponse.class
        );
        assertThat(newPwOk.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(newPwOk.getBody()).isNotNull();
        assertThat(newPwOk.getBody().requiresVerification()).isFalse();
        assertThat(newPwOk.getBody().accessToken()).isNotBlank();
    }
}
