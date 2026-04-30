package dev.ngb.app.identity.integration;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginRequest;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.VerifyLoginRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.ForgotPasswordRequest;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.ResetPasswordRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.ResendVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.VerifyEmailRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.LogoutAccountRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.RefreshTokenRequest;
import dev.ngb.app.identity.support.IdentityAuthApiClient;
import dev.ngb.app.identity.support.TestOtpSender;
import dev.ngb.app.support.AbstractIntegrationTest;
import dev.ngb.app.support.TestUtils;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HTTP integration tests for {@code /api/auth/*}, ordered around the flows documented under
 * {@code docs/auth/} (overview, registration-flow, login-flow, token-lifecycle, oauth-flow,
 * password-reset-flow). Uses {@link IdentityAuthApiClient}, {@link TestUtils}, and
 * {@link TestOtpSender}.
 */
@DisplayName("Auth API")
class IdentityAuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestOtpSender testOtpSender;

    private IdentityAuthApiClient identityAuth;

    @BeforeEach
    void setUp() {
        testOtpSender.clear();
        identityAuth = new IdentityAuthApiClient(objectMapper, restTemplate, baseUrl());
    }

    @Test
    @DisplayName("Registration: duplicate email → 409 EMAIL_ALREADY_EXISTS")
    void registrationRegisterThenDuplicateEmailReturnsConflict() {
        var email = TestUtils.getUniqueEmail();
        var body = new RegisterAccountRequest(email, "Password1!");

        var first = identityAuth.registerAccount(body);
        assertThat(first.isRight()).isTrue();
        assertThat(first.get().accountUuid()).isNotBlank();

        var second = identityAuth.registerAccount(body);
        assertThat(second.isLeft()).isTrue();
        assertThat(second.getLeft().error()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("Registration: register → resend OTP → verify-email")
    void registrationRegisterResendThenVerifyEmail() {
        var email = TestUtils.getUniqueEmail();
        var password = "Password1!";
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-reg-resend");

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, password)).isRight()).isTrue();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);

        assertThat(identityAuth.resendVerification(new ResendVerificationRequest(email)).isRight()).isTrue();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);

        var otpAfterResend = testOtpSender.getLastOtpCode().orElseThrow();
        var verifyResult =
                identityAuth.verifyEmail(new VerifyEmailRequest(email, otpAfterResend, device));
        assertThat(verifyResult.isRight()).isTrue();
        assertThat(verifyResult.get().accessToken()).isNotBlank();
        assertThat(verifyResult.get().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Registration: verify-email when already verified → 409")
    void registrationVerifyEmailWhenAlreadyVerifiedReturnsConflict() {
        var email = TestUtils.getUniqueEmail();
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-v1");

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, "Password1!")).isRight()).isTrue();
        var otp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.verifyEmail(new VerifyEmailRequest(email, otp, device)).isRight()).isTrue();

        var again = identityAuth.verifyEmail(new VerifyEmailRequest(email, otp, device));
        assertThat(again.isLeft()).isTrue();
        assertThat(again.getLeft().error()).isEqualTo("EMAIL_ALREADY_VERIFIED");
    }

    @Test
    @DisplayName("Registration: resend while pending → 204 + REGISTRATION OTP")
    void registrationResendVerificationWhenPendingSucceeds() {
        var email = TestUtils.getUniqueEmail();
        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, "Password1!")).isRight()).isTrue();

        assertThat(identityAuth.resendVerification(new ResendVerificationRequest(email)).isRight()).isTrue();
        assertThat(testOtpSender.getLastSent()).isPresent();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);
    }

    @Test
    @DisplayName("Login: wrong password → 401 INVALID_CREDENTIALS")
    void loginWithWrongPasswordReturnsUnauthorized() {
        var email = TestUtils.getUniqueEmail();
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-wp");

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, "Password1!")).isRight()).isTrue();
        var otp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.verifyEmail(new VerifyEmailRequest(email, otp, device)).isRight()).isTrue();

        var bad =
                identityAuth.login(new LoginAccountRequest(email, "wrong", device));
        assertThat(bad.isLeft()).isTrue();
        assertThat(bad.getLeft().error()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    @DisplayName("Login: new device → OTP → POST /login/verify → tokens")
    void loginNewDeviceRequiresOtpThenVerifyLoginIssuesTokens() {
        var email = TestUtils.getUniqueEmail();
        var password = "Password1!";
        var firstDevice = new DeviceInfo(DeviceType.WEB, "d1", "fp-first");
        var secondDevice = new DeviceInfo(DeviceType.WEB, "d2", "fp-second");

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, password)).isRight()).isTrue();
        var regOtp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.verifyEmail(new VerifyEmailRequest(email, regOtp, firstDevice)).isRight()).isTrue();

        var stepUp =
                identityAuth.login(new LoginAccountRequest(email, password, secondDevice));
        assertThat(stepUp.isRight()).isTrue();
        assertThat(stepUp.get().requiresVerification()).isTrue();
        assertThat(stepUp.get().verificationToken()).isNotBlank();

        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.LOGIN);
        var loginOtp = testOtpSender.getLastOtpCode().orElseThrow();

        var done =
                identityAuth.verifyLogin(new VerifyLoginRequest(stepUp.get().verificationToken(), loginOtp));
        assertThat(done.isRight()).isTrue();
        assertThat(done.get().accessToken()).isNotBlank();
        assertThat(done.get().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Token lifecycle: login → refresh → logout → refresh → 401")
    void tokenRefreshLogoutThenStaleRefreshReturnsUnauthorized() {
        var email = TestUtils.getUniqueEmail();
        var password = "Password1!";
        var device = new DeviceInfo(DeviceType.WEB, "integration-browser", "fp-known-1");

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, password)).isRight()).isTrue();

        assertThat(testOtpSender.getLastSent()).isPresent();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);
        var regOtp = testOtpSender.getLastOtpCode().orElseThrow();

        var verifyResp =
                identityAuth.verifyEmail(new VerifyEmailRequest(email, regOtp, device));
        assertThat(verifyResp.isRight()).isTrue();
        assertThat(verifyResp.get().accessToken()).isNotBlank();
        assertThat(verifyResp.get().refreshToken()).isNotBlank();

        var loginResp =
                identityAuth.login(new LoginAccountRequest(email, password, device));
        assertThat(loginResp.isRight()).isTrue();
        assertThat(loginResp.get().requiresVerification()).isFalse();
        assertThat(loginResp.get().accessToken()).isNotBlank();
        var refresh = loginResp.get().refreshToken();

        var refreshResp = identityAuth.refreshToken(new RefreshTokenRequest(refresh));
        assertThat(refreshResp.isRight()).isTrue();
        var newRefresh = refreshResp.get().refreshToken();

        assertThat(identityAuth.logout(new LogoutAccountRequest(newRefresh)).isRight()).isTrue();

        var staleRefresh = identityAuth.refreshToken(new RefreshTokenRequest(newRefresh));
        assertThat(staleRefresh.isLeft()).isTrue();
        assertThat(staleRefresh.getLeft().error()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    @DisplayName("OAuth: POST /oauth valid provider token → new account + tokens")
    void oauthLoginWithIntegrationTokenCreatesAccountAndReturnsTokens() {
        var device = new DeviceInfo(DeviceType.WEB, "oauth-client", "fp-oauth-1");
        var body = new OAuthLoginRequest(AuthProvider.GOOGLE, "integration-oauth-valid", device);

        var oauth = identityAuth.oauthLogin(body);
        assertThat(oauth.isRight()).isTrue();
        assertThat(oauth.get().isNewAccount()).isTrue();
        assertThat(oauth.get().accessToken()).isNotBlank();
        assertThat(oauth.get().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Password reset: forgot → reset → old password fails, new works")
    void passwordForgotThenResetAllowsLoginWithNewPassword() {
        var email = TestUtils.getUniqueEmail();
        var oldPassword = "Password1!";
        var newPassword = "Password2!";
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-reset");

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, oldPassword)).isRight()).isTrue();
        var regOtp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.verifyEmail(new VerifyEmailRequest(email, regOtp, device)).isRight()).isTrue();

        assertThat(identityAuth.forgotPassword(new ForgotPasswordRequest(email)).isRight()).isTrue();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.PASSWORD_RESET);
        var resetOtp = testOtpSender.getLastOtpCode().orElseThrow();

        assertThat(identityAuth.resetPassword(new ResetPasswordRequest(email, resetOtp, newPassword)).isRight()).isTrue();

        var oldPwFails =
                identityAuth.login(new LoginAccountRequest(email, oldPassword, device));
        assertThat(oldPwFails.isLeft()).isTrue();
        assertThat(oldPwFails.getLeft().error()).isEqualTo("INVALID_CREDENTIALS");

        var newPwOk =
                identityAuth.login(new LoginAccountRequest(email, newPassword, device));
        assertThat(newPwOk.isRight()).isTrue();
        assertThat(newPwOk.get().requiresVerification()).isFalse();
        assertThat(newPwOk.get().accessToken()).isNotBlank();
    }
}
