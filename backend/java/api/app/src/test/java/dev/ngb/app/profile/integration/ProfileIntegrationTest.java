package dev.ngb.app.profile.integration;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.VerifyEmailRequest;
import dev.ngb.app.identity.support.IdentityAuthTestClient;
import dev.ngb.app.identity.support.TestOtpSender;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.profile.support.ProfileTestClient;
import dev.ngb.app.support.AbstractIntegrationTest;
import dev.ngb.app.support.TestUtils;
import dev.ngb.domain.identity.model.auth.DeviceType;
import dev.ngb.web.ErrorResponse;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Profile API")
class ProfileIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestOtpSender testOtpSender;

    private IdentityAuthTestClient identityAuth;
    private ProfileTestClient profiles;

    @BeforeEach
    void setUp() {
        testOtpSender.clear();
        identityAuth = new IdentityAuthTestClient(objectMapper, restTemplate, baseUrl());
        profiles = new ProfileTestClient(objectMapper, restTemplate, baseUrl());
    }

    @Test
    @DisplayName("POST /api/profiles without token -> 401")
    void createProfileWithoutAuthReturnsUnauthorized() {
        Either<ErrorResponse, CreateProfileResponse> result = profiles.createProfile(
                new CreateProfileRequest("guest.user", "Guest User", null, null)
        );
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    @DisplayName("Active account can create multiple profiles")
    void createProfileWithActiveAccountAllowsMultipleProfiles() {
        String accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        Either<ErrorResponse, CreateProfileResponse> first = profiles.createProfile(
                new CreateProfileRequest("multi.user.one", "Multi User One", "bio one", null),
                headers
        );
        assertThat(first.isRight()).isTrue();
        assertThat(first.get().username()).isEqualTo("multi.user.one");

        Either<ErrorResponse, CreateProfileResponse> second = profiles.createProfile(
                new CreateProfileRequest("multi.user.two", "Multi User Two", "bio two", null),
                headers
        );
        assertThat(second.isRight()).isTrue();
        assertThat(second.get().username()).isEqualTo("multi.user.two");
    }

    @Test
    @DisplayName("Duplicate username -> 409 USERNAME_ALREADY_EXISTS")
    void createProfileDuplicateUsernameReturnsConflict() {
        String accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        assertThat(profiles.createProfile(
                new CreateProfileRequest("taken.profile", "Taken Profile", null, null),
                headers
        ).isRight()).isTrue();

        Either<ErrorResponse, CreateProfileResponse> duplicate = profiles.createProfile(
                new CreateProfileRequest("taken.profile", "Another Display Name", null, null),
                headers
        );
        assertThat(duplicate.isLeft()).isTrue();
        assertThat(duplicate.getLeft().error()).isEqualTo("USERNAME_ALREADY_EXISTS");
    }

    private String registerAndVerifyToGetAccessToken() {
        String email = TestUtils.getUniqueEmail();
        String password = "Password1!";
        DeviceInfo device = new DeviceInfo(DeviceType.WEB, "profile-test-browser", "profile-fp-" + System.nanoTime());

        assertThat(identityAuth.registerAccount(new RegisterAccountRequest(email, password)).isRight()).isTrue();

        String otp = testOtpSender.getLastOtpCode().orElseThrow();
        var verify = identityAuth.verifyEmail(new VerifyEmailRequest(email, otp, device));
        assertThat(verify.isRight()).isTrue();
        return verify.get().accessToken();
    }
}
