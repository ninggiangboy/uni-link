package dev.ngb.app.identity.dto;

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
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.util.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Identity DTO Validation")
class IdentityDtoValidationTest {

    private static final String EMAIL = IdentityUseCaseTestFixtures.EMAIL;
    private static final String FINGERPRINT = IdentityUseCaseTestFixtures.FINGERPRINT;
    private static final String DEVICE_NAME = "chrome";
    private static final DeviceType DEVICE_TYPE = DeviceType.WEB;
    private static final AuthProvider PROVIDER = AuthProvider.GOOGLE;
    private static final String PASSWORD = "secret123";
    private static final String OTP_CODE = "123456";
    private static final String TOKEN = "some-token-value";
    private static final String VERIFICATION_TOKEN = "verification-token-xyz";

    private DeviceInfo validDeviceInfo() {
        return new DeviceInfo(DEVICE_TYPE, DEVICE_NAME, FINGERPRINT);
    }

    private void assertFieldError(Executable executable, String expectedField, String expectedMessage) {
        var ex = assertThrows(ValidationException.class, executable);
        assertThat(ex.errors()).anySatisfy(e -> {
            assertThat(e.field()).isEqualTo(expectedField);
            assertThat(e.message()).isEqualTo(expectedMessage);
        });
    }

    @Nested
    @DisplayName("RegisterAccountRequest")
    class RegisterAccountRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new RegisterAccountRequest(EMAIL, PASSWORD));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new RegisterAccountRequest(null, PASSWORD),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank email → 'must not be null or blank'")
        void blankEmailThrows() {
            assertFieldError(() -> new RegisterAccountRequest("   ", PASSWORD),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new RegisterAccountRequest("not-an-email", PASSWORD),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Email with spaces in middle → 'must be a valid email'")
        void emailWithSpacesInMiddleThrows() {
            assertFieldError(() -> new RegisterAccountRequest("user @test.com", PASSWORD),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Null password → 'must not be null or blank'")
        void nullPasswordThrows() {
            assertFieldError(() -> new RegisterAccountRequest(EMAIL, null),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank password → 'must not be null or blank'")
        void blankPasswordThrows() {
            assertFieldError(() -> new RegisterAccountRequest(EMAIL, "   "),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims email and password")
        void trimsEmailAndPassword() {
            var request = new RegisterAccountRequest("  " + EMAIL + "  ", "  " + PASSWORD + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
            assertThat(request.password()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("VerifyEmailRequest")
    class VerifyEmailRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new VerifyEmailRequest(EMAIL, OTP_CODE, validDeviceInfo()));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new VerifyEmailRequest(null, OTP_CODE, validDeviceInfo()),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new VerifyEmailRequest("bad-email", OTP_CODE, validDeviceInfo()),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Null otpCode → 'must not be null or blank'")
        void nullOtpCodeThrows() {
            assertFieldError(() -> new VerifyEmailRequest(EMAIL, null, validDeviceInfo()),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank otpCode → 'must not be null or blank'")
        void blankOtpCodeThrows() {
            assertFieldError(() -> new VerifyEmailRequest(EMAIL, "   ", validDeviceInfo()),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Null deviceInfo → 'must not be null'")
        void nullDeviceInfoThrows() {
            assertFieldError(() -> new VerifyEmailRequest(EMAIL, OTP_CODE, null),
                    "deviceInfo", "must not be null");
        }

        @Test
        @DisplayName("Trims email and otpCode")
        void trimsEmailAndOtpCode() {
            var request = new VerifyEmailRequest("  " + EMAIL + "  ", "  " + OTP_CODE + "  ", validDeviceInfo());
            assertThat(request.email()).isEqualTo(EMAIL);
            assertThat(request.otpCode()).isEqualTo(OTP_CODE);
        }
    }

    @Nested
    @DisplayName("ResendVerificationRequest")
    class ResendVerificationRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new ResendVerificationRequest(EMAIL));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new ResendVerificationRequest(null),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank email → 'must not be null or blank'")
        void blankEmailThrows() {
            assertFieldError(() -> new ResendVerificationRequest("   "),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new ResendVerificationRequest("not-an-email"),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Trims email")
        void trimsEmail() {
            var request = new ResendVerificationRequest("  " + EMAIL + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("LoginAccountRequest")
    class LoginAccountRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new LoginAccountRequest(EMAIL, PASSWORD, validDeviceInfo()));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new LoginAccountRequest(null, PASSWORD, validDeviceInfo()),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new LoginAccountRequest("bad", PASSWORD, validDeviceInfo()),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Null password → 'must not be null or blank'")
        void nullPasswordThrows() {
            assertFieldError(() -> new LoginAccountRequest(EMAIL, null, validDeviceInfo()),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank password → 'must not be null or blank'")
        void blankPasswordThrows() {
            assertFieldError(() -> new LoginAccountRequest(EMAIL, "   ", validDeviceInfo()),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Null deviceInfo → 'must not be null'")
        void nullDeviceInfoThrows() {
            assertFieldError(() -> new LoginAccountRequest(EMAIL, PASSWORD, null),
                    "deviceInfo", "must not be null");
        }

        @Test
        @DisplayName("Trims email and password")
        void trimsEmailAndPassword() {
            var request = new LoginAccountRequest("  " + EMAIL + "  ", "  " + PASSWORD + "  ", validDeviceInfo());
            assertThat(request.email()).isEqualTo(EMAIL);
            assertThat(request.password()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("VerifyLoginRequest")
    class VerifyLoginRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new VerifyLoginRequest(VERIFICATION_TOKEN, OTP_CODE));
        }

        @Test
        @DisplayName("Null verificationToken → 'must not be null or blank'")
        void nullVerificationTokenThrows() {
            assertFieldError(() -> new VerifyLoginRequest(null, OTP_CODE),
                    "verificationToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank verificationToken → 'must not be null or blank'")
        void blankVerificationTokenThrows() {
            assertFieldError(() -> new VerifyLoginRequest("   ", OTP_CODE),
                    "verificationToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Null otpCode → 'must not be null or blank'")
        void nullOtpCodeThrows() {
            assertFieldError(() -> new VerifyLoginRequest(VERIFICATION_TOKEN, null),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank otpCode → 'must not be null or blank'")
        void blankOtpCodeThrows() {
            assertFieldError(() -> new VerifyLoginRequest(VERIFICATION_TOKEN, "   "),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims both fields")
        void trimsBothFields() {
            var request = new VerifyLoginRequest("  " + VERIFICATION_TOKEN + "  ", "  " + OTP_CODE + "  ");
            assertThat(request.verificationToken()).isEqualTo(VERIFICATION_TOKEN);
            assertThat(request.otpCode()).isEqualTo(OTP_CODE);
        }
    }

    @Nested
    @DisplayName("OAuthLoginRequest")
    class OAuthLoginRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new OAuthLoginRequest(PROVIDER, TOKEN, validDeviceInfo()));
        }

        @Test
        @DisplayName("Null provider → 'must not be null'")
        void nullProviderThrows() {
            assertFieldError(() -> new OAuthLoginRequest(null, TOKEN, validDeviceInfo()),
                    "provider", "must not be null");
        }

        @Test
        @DisplayName("Null providerToken → 'must not be null or blank'")
        void nullProviderTokenThrows() {
            assertFieldError(() -> new OAuthLoginRequest(PROVIDER, null, validDeviceInfo()),
                    "providerToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank providerToken → 'must not be null or blank'")
        void blankProviderTokenThrows() {
            assertFieldError(() -> new OAuthLoginRequest(PROVIDER, "   ", validDeviceInfo()),
                    "providerToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Null deviceInfo → 'must not be null'")
        void nullDeviceInfoThrows() {
            assertFieldError(() -> new OAuthLoginRequest(PROVIDER, TOKEN, null),
                    "deviceInfo", "must not be null");
        }

        @Test
        @DisplayName("Trims providerToken")
        void trimsProviderToken() {
            var request = new OAuthLoginRequest(PROVIDER, "  " + TOKEN + "  ", validDeviceInfo());
            assertThat(request.providerToken()).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("RefreshTokenRequest")
    class RefreshTokenRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new RefreshTokenRequest(TOKEN));
        }

        @Test
        @DisplayName("Null refreshToken → 'must not be null or blank'")
        void nullRefreshTokenThrows() {
            assertFieldError(() -> new RefreshTokenRequest(null),
                    "refreshToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank refreshToken → 'must not be null or blank'")
        void blankRefreshTokenThrows() {
            assertFieldError(() -> new RefreshTokenRequest("   "),
                    "refreshToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims refreshToken")
        void trimsRefreshToken() {
            var request = new RefreshTokenRequest("  " + TOKEN + "  ");
            assertThat(request.refreshToken()).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("LogoutAccountRequest")
    class LogoutAccountRequestTest {

        @Test
        @DisplayName("Valid token → success")
        void validTokenSucceeds() {
            var request = new LogoutAccountRequest(TOKEN);
            assertThat(request.refreshToken()).isEqualTo(TOKEN);
        }

        @Test
        @DisplayName("Null token → success (no validation)")
        void nullTokenSucceeds() {
            var request = new LogoutAccountRequest(null);
            assertThat(request.refreshToken()).isNull();
        }

        @Test
        @DisplayName("Blank token → sets to null")
        void blankTokenBecomesNull() {
            var request = new LogoutAccountRequest("   ");
            assertThat(request.refreshToken()).isNull();
        }

        @Test
        @DisplayName("Trims token")
        void trimsToken() {
            var request = new LogoutAccountRequest("  " + TOKEN + "  ");
            assertThat(request.refreshToken()).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("ForgotPasswordRequest")
    class ForgotPasswordRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new ForgotPasswordRequest(EMAIL));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new ForgotPasswordRequest(null),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank email → 'must not be null or blank'")
        void blankEmailThrows() {
            assertFieldError(() -> new ForgotPasswordRequest("   "),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new ForgotPasswordRequest("not-an-email"),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Trims email")
        void trimsEmail() {
            var request = new ForgotPasswordRequest("  " + EMAIL + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("ResetPasswordRequest")
    class ResetPasswordRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new ResetPasswordRequest(EMAIL, OTP_CODE, PASSWORD));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new ResetPasswordRequest(null, OTP_CODE, PASSWORD),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new ResetPasswordRequest("bad", OTP_CODE, PASSWORD),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Null otpCode → 'must not be null or blank'")
        void nullOtpCodeThrows() {
            assertFieldError(() -> new ResetPasswordRequest(EMAIL, null, PASSWORD),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank otpCode → 'must not be null or blank'")
        void blankOtpCodeThrows() {
            assertFieldError(() -> new ResetPasswordRequest(EMAIL, "   ", PASSWORD),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Null newPassword → 'must not be null or blank'")
        void nullNewPasswordThrows() {
            assertFieldError(() -> new ResetPasswordRequest(EMAIL, OTP_CODE, null),
                    "newPassword", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank newPassword → 'must not be null or blank'")
        void blankNewPasswordThrows() {
            assertFieldError(() -> new ResetPasswordRequest(EMAIL, OTP_CODE, "   "),
                    "newPassword", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims all fields")
        void trimsAllFields() {
            var request = new ResetPasswordRequest("  " + EMAIL + "  ", "  " + OTP_CODE + "  ", "  " + PASSWORD + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
            assertThat(request.otpCode()).isEqualTo(OTP_CODE);
            assertThat(request.newPassword()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("DeviceInfo")
    class DeviceInfoTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new DeviceInfo(DEVICE_TYPE, DEVICE_NAME, FINGERPRINT));
        }

        @Test
        @DisplayName("Null deviceType → 'must not be null'")
        void nullDeviceTypeThrows() {
            assertFieldError(() -> new DeviceInfo(null, DEVICE_NAME, FINGERPRINT),
                    "deviceType", "must not be null");
        }

        @Test
        @DisplayName("Null deviceName → 'must not be null or blank'")
        void nullDeviceNameThrows() {
            assertFieldError(() -> new DeviceInfo(DEVICE_TYPE, null, FINGERPRINT),
                    "deviceName", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank deviceName → 'must not be null or blank'")
        void blankDeviceNameThrows() {
            assertFieldError(() -> new DeviceInfo(DEVICE_TYPE, "   ", FINGERPRINT),
                    "deviceName", "must not be null or blank");
        }

        @Test
        @DisplayName("Null fingerprint → 'must not be null or blank'")
        void nullFingerprintThrows() {
            assertFieldError(() -> new DeviceInfo(DEVICE_TYPE, DEVICE_NAME, null),
                    "fingerprint", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank fingerprint → 'must not be null or blank'")
        void blankFingerprintThrows() {
            assertFieldError(() -> new DeviceInfo(DEVICE_TYPE, DEVICE_NAME, "   "),
                    "fingerprint", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims deviceName and fingerprint")
        void trimsDeviceNameAndFingerprint() {
            var deviceInfo = new DeviceInfo(DEVICE_TYPE, "  " + DEVICE_NAME + "  ", "  " + FINGERPRINT + "  ");
            assertThat(deviceInfo.deviceName()).isEqualTo(DEVICE_NAME);
            assertThat(deviceInfo.fingerprint()).isEqualTo(FINGERPRINT);
        }
    }
}
