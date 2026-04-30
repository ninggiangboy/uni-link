package dev.ngb.app.profile.integration;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.support.IdentityAuthApiClient;
import dev.ngb.app.identity.support.TestOtpSender;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.support.ProfileApiClient;
import dev.ngb.app.support.AbstractIntegrationTest;
import dev.ngb.app.support.TestUtils;
import dev.ngb.domain.identity.model.auth.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Profile API")
class ProfileIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestOtpSender testOtpSender;

    private IdentityAuthApiClient identityAuth;
    private ProfileApiClient profiles;

    @BeforeEach
    void setUp() {
        testOtpSender.clear();
        identityAuth = new IdentityAuthApiClient(objectMapper, restTemplate, baseUrl());
        profiles = new ProfileApiClient(objectMapper, restTemplate, baseUrl());
    }

    @Test
    @DisplayName("POST /api/profiles without token -> 401")
    void createProfileWithoutAuthReturnsUnauthorized() {
        var result = profiles.createProfile(
                new CreateProfileRequest("guest.user", "Guest User", null, null)
        );
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    @DisplayName("Active account can create multiple profiles")
    void createProfileWithActiveAccountAllowsMultipleProfiles() {
        var accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        var first = profiles.createProfile(
                new CreateProfileRequest("multi.user.one", "Multi User One", "bio one", null),
                headers
        );
        assertThat(first.isRight()).isTrue();
        assertThat(first.get().username()).isEqualTo("multi.user.one");

        var second = profiles.createProfile(
                new CreateProfileRequest("multi.user.two", "Multi User Two", "bio two", null),
                headers
        );
        assertThat(second.isRight()).isTrue();
        assertThat(second.get().username()).isEqualTo("multi.user.two");
    }

    @Test
    @DisplayName("Duplicate username -> 409 USERNAME_ALREADY_EXISTS")
    void createProfileDuplicateUsernameReturnsConflict() {
        var accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        assertThat(profiles.createProfile(
                new CreateProfileRequest("taken.profile", "Taken Profile", null, null),
                headers
        ).isRight()).isTrue();

        var duplicate = profiles.createProfile(
                new CreateProfileRequest("taken.profile", "Another Display Name", null, null),
                headers
        );
        assertThat(duplicate.isLeft()).isTrue();
        assertThat(duplicate.getLeft().error()).isEqualTo("USERNAME_ALREADY_EXISTS");
    }

    private String registerAndVerifyToGetAccessToken() {
        var email = TestUtils.getUniqueEmail();
        var password = "Password1!";
        var device = new DeviceInfo(DeviceType.WEB, "profile-test-browser", "profile-fp-" + System.nanoTime());

        var register = identityAuth.createAccount(new CreateAccountRequest(email, password));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();

        var otp = testOtpSender.getLastOtpCode().orElseThrow();
        var verify = identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(otp, device));
        assertThat(verify.isRight()).isTrue();
        return verify.get().accessToken();
    }
}
