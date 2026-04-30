package dev.ngb.app.identity.integration;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.CompleteSessionVerificationRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.DeleteCurrentSessionRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.CreateTokenRequest;
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
 * HTTP integration tests for identity REST endpoints, ordered around the flows documented under
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
        var body = new CreateAccountRequest(email, "Password1!");

        var first = identityAuth.createAccount(body);
        assertThat(first.isRight()).isTrue();
        assertThat(first.get().accountUuid()).isNotBlank();

        var second = identityAuth.createAccount(body);
        assertThat(second.isLeft()).isTrue();
        assertThat(second.getLeft().error()).isEqualTo("EMAIL_ALREADY_EXISTS");
    }

    @Test
    @DisplayName("Registration: register → resend OTP → verify-email")
    void registrationRegisterResendThenVerifyEmail() {
        var email = TestUtils.getUniqueEmail();
        var password = "Password1!";
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-reg-resend");

        var register = identityAuth.createAccount(new CreateAccountRequest(email, password));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);

        var resend = identityAuth.createEmailVerification(new CreateEmailVerificationRequest(email));
        assertThat(resend.isRight()).isTrue();
        verificationId = resend.get().verificationId();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);

        var otpAfterResend = testOtpSender.getLastOtpCode().orElseThrow();
        var verifyResult =
                identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(otpAfterResend, device));
        assertThat(verifyResult.isRight()).isTrue();
        assertThat(verifyResult.get().accessToken()).isNotBlank();
        assertThat(verifyResult.get().refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("Registration: verify-email when already verified → 409")
    void registrationVerifyEmailWhenAlreadyVerifiedReturnsConflict() {
        var email = TestUtils.getUniqueEmail();
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-v1");

        var register = identityAuth.createAccount(new CreateAccountRequest(email, "Password1!"));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();
        var otp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(otp, device)).isRight()).isTrue();

        var again = identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(otp, device));
        assertThat(again.isLeft()).isTrue();
        assertThat(again.getLeft().error()).isEqualTo("EMAIL_ALREADY_VERIFIED");
    }

    @Test
    @DisplayName("Registration: resend while pending → 204 + REGISTRATION OTP")
    void registrationResendVerificationWhenPendingSucceeds() {
        var email = TestUtils.getUniqueEmail();
        var register = identityAuth.createAccount(new CreateAccountRequest(email, "Password1!"));
        assertThat(register.isRight()).isTrue();

        assertThat(identityAuth.createEmailVerification(new CreateEmailVerificationRequest(email)).isRight()).isTrue();
        assertThat(testOtpSender.getLastSent()).isPresent();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);
    }

    @Test
    @DisplayName("Login: wrong password → 401 INVALID_CREDENTIALS")
    void loginWithWrongPasswordReturnsUnauthorized() {
        var email = TestUtils.getUniqueEmail();
        var device = new DeviceInfo(DeviceType.WEB, "chrome", "fp-wp");

        var register = identityAuth.createAccount(new CreateAccountRequest(email, "Password1!"));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();
        var otp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(otp, device)).isRight()).isTrue();

        var bad =
                identityAuth.createSession(new CreateSessionRequest(email, "wrong", device));
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

        var register = identityAuth.createAccount(new CreateAccountRequest(email, password));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();
        var regOtp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(regOtp, firstDevice)).isRight()).isTrue();

        var stepUp =
                identityAuth.createSession(new CreateSessionRequest(email, password, secondDevice));
        assertThat(stepUp.isRight()).isTrue();
        assertThat(stepUp.get().requiresVerification()).isTrue();
        assertThat(stepUp.get().verificationToken()).isNotBlank();

        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.LOGIN);
        var loginOtp = testOtpSender.getLastOtpCode().orElseThrow();

        var done =
                identityAuth.completeSessionVerification(new CompleteSessionVerificationRequest(stepUp.get().verificationToken(), loginOtp));
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

        var register = identityAuth.createAccount(new CreateAccountRequest(email, password));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();

        assertThat(testOtpSender.getLastSent()).isPresent();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.REGISTRATION);
        var regOtp = testOtpSender.getLastOtpCode().orElseThrow();

        var verifyResp =
                identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(regOtp, device));
        assertThat(verifyResp.isRight()).isTrue();
        assertThat(verifyResp.get().accessToken()).isNotBlank();
        assertThat(verifyResp.get().refreshToken()).isNotBlank();

        var loginResp =
                identityAuth.createSession(new CreateSessionRequest(email, password, device));
        assertThat(loginResp.isRight()).isTrue();
        assertThat(loginResp.get().requiresVerification()).isFalse();
        assertThat(loginResp.get().accessToken()).isNotBlank();
        var refresh = loginResp.get().refreshToken();

        var refreshResp = identityAuth.createToken(new CreateTokenRequest(refresh));
        assertThat(refreshResp.isRight()).isTrue();
        var newRefresh = refreshResp.get().refreshToken();

        assertThat(identityAuth.deleteCurrentSession(new DeleteCurrentSessionRequest(newRefresh)).isRight()).isTrue();

        var staleRefresh = identityAuth.createToken(new CreateTokenRequest(newRefresh));
        assertThat(staleRefresh.isLeft()).isTrue();
        assertThat(staleRefresh.getLeft().error()).isEqualTo("INVALID_REFRESH_TOKEN");
    }

    @Test
    @DisplayName("OAuth: POST /oauth valid provider token → new account + tokens")
    void oauthLoginWithIntegrationTokenCreatesAccountAndReturnsTokens() {
        var device = new DeviceInfo(DeviceType.WEB, "oauth-client", "fp-oauth-1");
        var body = new CreateOAuthSessionRequest(AuthProvider.GOOGLE, "integration-oauth-valid", device);

        var oauth = identityAuth.createOAuthSession(body);
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

        var register = identityAuth.createAccount(new CreateAccountRequest(email, oldPassword));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();
        var regOtp = testOtpSender.getLastOtpCode().orElseThrow();
        assertThat(identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(regOtp, device)).isRight()).isTrue();

        var forgot = identityAuth.createPasswordReset(new CreatePasswordResetRequest(email));
        assertThat(forgot.isRight()).isTrue();
        var resetId = forgot.get().resetId();
        assertThat(testOtpSender.getLastSent().orElseThrow().purpose()).isEqualTo(OtpPurpose.PASSWORD_RESET);
        var resetOtp = testOtpSender.getLastOtpCode().orElseThrow();

        assertThat(identityAuth.completePasswordReset(resetId, new CompletePasswordResetRequest(resetOtp, newPassword)).isRight()).isTrue();

        var oldPwFails =
                identityAuth.createSession(new CreateSessionRequest(email, oldPassword, device));
        assertThat(oldPwFails.isLeft()).isTrue();
        assertThat(oldPwFails.getLeft().error()).isEqualTo("INVALID_CREDENTIALS");

        var newPwOk =
                identityAuth.createSession(new CreateSessionRequest(email, newPassword, device));
        assertThat(newPwOk.isRight()).isTrue();
        assertThat(newPwOk.get().requiresVerification()).isFalse();
        assertThat(newPwOk.get().accessToken()).isNotBlank();
    }
}
