package dev.ngb.app.identity.dto;

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
    @DisplayName("CreateAccountRequest")
    class CreateAccountRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CreateAccountRequest(EMAIL, PASSWORD));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new CreateAccountRequest(null, PASSWORD),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank email → 'must not be null or blank'")
        void blankEmailThrows() {
            assertFieldError(() -> new CreateAccountRequest("   ", PASSWORD),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new CreateAccountRequest("not-an-email", PASSWORD),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Email with spaces in middle → 'must be a valid email'")
        void emailWithSpacesInMiddleThrows() {
            assertFieldError(() -> new CreateAccountRequest("user @test.com", PASSWORD),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Null password → 'must not be null or blank'")
        void nullPasswordThrows() {
            assertFieldError(() -> new CreateAccountRequest(EMAIL, null),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank password → 'must not be null or blank'")
        void blankPasswordThrows() {
            assertFieldError(() -> new CreateAccountRequest(EMAIL, "   "),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims email and password")
        void trimsEmailAndPassword() {
            var request = new CreateAccountRequest("  " + EMAIL + "  ", "  " + PASSWORD + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
            assertThat(request.password()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("CompleteEmailVerificationRequest")
    class CompleteEmailVerificationRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CompleteEmailVerificationRequest(OTP_CODE, validDeviceInfo()));
        }

        @Test
        @DisplayName("Null otpCode → 'must not be null or blank'")
        void nullOtpCodeThrows() {
            assertFieldError(() -> new CompleteEmailVerificationRequest(null, validDeviceInfo()),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank otpCode → 'must not be null or blank'")
        void blankOtpCodeThrows() {
            assertFieldError(() -> new CompleteEmailVerificationRequest("   ", validDeviceInfo()),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Null deviceInfo → 'must not be null'")
        void nullDeviceInfoThrows() {
            assertFieldError(() -> new CompleteEmailVerificationRequest(OTP_CODE, null),
                    "deviceInfo", "must not be null");
        }

        @Test
        @DisplayName("Trims otpCode")
        void trimsOtpCode() {
            var request = new CompleteEmailVerificationRequest("  " + OTP_CODE + "  ", validDeviceInfo());
            assertThat(request.otpCode()).isEqualTo(OTP_CODE);
        }
    }

    @Nested
    @DisplayName("CreateEmailVerificationRequest")
    class CreateEmailVerificationRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CreateEmailVerificationRequest(EMAIL));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new CreateEmailVerificationRequest(null),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank email → 'must not be null or blank'")
        void blankEmailThrows() {
            assertFieldError(() -> new CreateEmailVerificationRequest("   "),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new CreateEmailVerificationRequest("not-an-email"),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Trims email")
        void trimsEmail() {
            var request = new CreateEmailVerificationRequest("  " + EMAIL + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("CreateSessionRequest")
    class CreateSessionRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CreateSessionRequest(EMAIL, PASSWORD, validDeviceInfo()));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new CreateSessionRequest(null, PASSWORD, validDeviceInfo()),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new CreateSessionRequest("bad", PASSWORD, validDeviceInfo()),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Null password → 'must not be null or blank'")
        void nullPasswordThrows() {
            assertFieldError(() -> new CreateSessionRequest(EMAIL, null, validDeviceInfo()),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank password → 'must not be null or blank'")
        void blankPasswordThrows() {
            assertFieldError(() -> new CreateSessionRequest(EMAIL, "   ", validDeviceInfo()),
                    "password", "must not be null or blank");
        }

        @Test
        @DisplayName("Null deviceInfo → 'must not be null'")
        void nullDeviceInfoThrows() {
            assertFieldError(() -> new CreateSessionRequest(EMAIL, PASSWORD, null),
                    "deviceInfo", "must not be null");
        }

        @Test
        @DisplayName("Trims email and password")
        void trimsEmailAndPassword() {
            var request = new CreateSessionRequest("  " + EMAIL + "  ", "  " + PASSWORD + "  ", validDeviceInfo());
            assertThat(request.email()).isEqualTo(EMAIL);
            assertThat(request.password()).isEqualTo(PASSWORD);
        }
    }

    @Nested
    @DisplayName("CompleteSessionVerificationRequest")
    class CompleteSessionVerificationRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CompleteSessionVerificationRequest(VERIFICATION_TOKEN, OTP_CODE));
        }

        @Test
        @DisplayName("Null verificationToken → 'must not be null or blank'")
        void nullVerificationTokenThrows() {
            assertFieldError(() -> new CompleteSessionVerificationRequest(null, OTP_CODE),
                    "verificationToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank verificationToken → 'must not be null or blank'")
        void blankVerificationTokenThrows() {
            assertFieldError(() -> new CompleteSessionVerificationRequest("   ", OTP_CODE),
                    "verificationToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Null otpCode → 'must not be null or blank'")
        void nullOtpCodeThrows() {
            assertFieldError(() -> new CompleteSessionVerificationRequest(VERIFICATION_TOKEN, null),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank otpCode → 'must not be null or blank'")
        void blankOtpCodeThrows() {
            assertFieldError(() -> new CompleteSessionVerificationRequest(VERIFICATION_TOKEN, "   "),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims both fields")
        void trimsBothFields() {
            var request = new CompleteSessionVerificationRequest("  " + VERIFICATION_TOKEN + "  ", "  " + OTP_CODE + "  ");
            assertThat(request.verificationToken()).isEqualTo(VERIFICATION_TOKEN);
            assertThat(request.otpCode()).isEqualTo(OTP_CODE);
        }
    }

    @Nested
    @DisplayName("CreateOAuthSessionRequest")
    class CreateOAuthSessionRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CreateOAuthSessionRequest(PROVIDER, TOKEN, validDeviceInfo()));
        }

        @Test
        @DisplayName("Null provider → 'must not be null'")
        void nullProviderThrows() {
            assertFieldError(() -> new CreateOAuthSessionRequest(null, TOKEN, validDeviceInfo()),
                    "provider", "must not be null");
        }

        @Test
        @DisplayName("Null providerToken → 'must not be null or blank'")
        void nullProviderTokenThrows() {
            assertFieldError(() -> new CreateOAuthSessionRequest(PROVIDER, null, validDeviceInfo()),
                    "providerToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank providerToken → 'must not be null or blank'")
        void blankProviderTokenThrows() {
            assertFieldError(() -> new CreateOAuthSessionRequest(PROVIDER, "   ", validDeviceInfo()),
                    "providerToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Null deviceInfo → 'must not be null'")
        void nullDeviceInfoThrows() {
            assertFieldError(() -> new CreateOAuthSessionRequest(PROVIDER, TOKEN, null),
                    "deviceInfo", "must not be null");
        }

        @Test
        @DisplayName("Trims providerToken")
        void trimsProviderToken() {
            var request = new CreateOAuthSessionRequest(PROVIDER, "  " + TOKEN + "  ", validDeviceInfo());
            assertThat(request.providerToken()).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("CreateTokenRequest")
    class CreateTokenRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CreateTokenRequest(TOKEN));
        }

        @Test
        @DisplayName("Null refreshToken → 'must not be null or blank'")
        void nullRefreshTokenThrows() {
            assertFieldError(() -> new CreateTokenRequest(null),
                    "refreshToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank refreshToken → 'must not be null or blank'")
        void blankRefreshTokenThrows() {
            assertFieldError(() -> new CreateTokenRequest("   "),
                    "refreshToken", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims refreshToken")
        void trimsRefreshToken() {
            var request = new CreateTokenRequest("  " + TOKEN + "  ");
            assertThat(request.refreshToken()).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("DeleteCurrentSessionRequest")
    class DeleteCurrentSessionRequestTest {

        @Test
        @DisplayName("Valid token → success")
        void validTokenSucceeds() {
            var request = new DeleteCurrentSessionRequest(TOKEN);
            assertThat(request.refreshToken()).isEqualTo(TOKEN);
        }

        @Test
        @DisplayName("Null token → success (no validation)")
        void nullTokenSucceeds() {
            var request = new DeleteCurrentSessionRequest(null);
            assertThat(request.refreshToken()).isNull();
        }

        @Test
        @DisplayName("Blank token → sets to null")
        void blankTokenBecomesNull() {
            var request = new DeleteCurrentSessionRequest("   ");
            assertThat(request.refreshToken()).isNull();
        }

        @Test
        @DisplayName("Trims token")
        void trimsToken() {
            var request = new DeleteCurrentSessionRequest("  " + TOKEN + "  ");
            assertThat(request.refreshToken()).isEqualTo(TOKEN);
        }
    }

    @Nested
    @DisplayName("CreatePasswordResetRequest")
    class CreatePasswordResetRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CreatePasswordResetRequest(EMAIL));
        }

        @Test
        @DisplayName("Null email → 'must not be null or blank'")
        void nullEmailThrows() {
            assertFieldError(() -> new CreatePasswordResetRequest(null),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank email → 'must not be null or blank'")
        void blankEmailThrows() {
            assertFieldError(() -> new CreatePasswordResetRequest("   "),
                    "email", "must not be null or blank");
        }

        @Test
        @DisplayName("Invalid email format → 'must be a valid email'")
        void invalidEmailFormatThrows() {
            assertFieldError(() -> new CreatePasswordResetRequest("not-an-email"),
                    "email", "must be a valid email");
        }

        @Test
        @DisplayName("Trims email")
        void trimsEmail() {
            var request = new CreatePasswordResetRequest("  " + EMAIL + "  ");
            assertThat(request.email()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("CompletePasswordResetRequest")
    class CompletePasswordResetRequestTest {

        @Test
        @DisplayName("Valid input → success")
        void validInputSucceeds() {
            assertDoesNotThrow(() -> new CompletePasswordResetRequest(OTP_CODE, PASSWORD));
        }

        @Test
        @DisplayName("Null otpCode → 'must not be null or blank'")
        void nullOtpCodeThrows() {
            assertFieldError(() -> new CompletePasswordResetRequest(null, PASSWORD),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank otpCode → 'must not be null or blank'")
        void blankOtpCodeThrows() {
            assertFieldError(() -> new CompletePasswordResetRequest("   ", PASSWORD),
                    "otpCode", "must not be null or blank");
        }

        @Test
        @DisplayName("Null newPassword → 'must not be null or blank'")
        void nullNewPasswordThrows() {
            assertFieldError(() -> new CompletePasswordResetRequest(OTP_CODE, null),
                    "newPassword", "must not be null or blank");
        }

        @Test
        @DisplayName("Blank newPassword → 'must not be null or blank'")
        void blankNewPasswordThrows() {
            assertFieldError(() -> new CompletePasswordResetRequest(OTP_CODE, "   "),
                    "newPassword", "must not be null or blank");
        }

        @Test
        @DisplayName("Trims all fields")
        void trimsAllFields() {
            var request = new CompletePasswordResetRequest("  " + OTP_CODE + "  ", "  " + PASSWORD + "  ");
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
