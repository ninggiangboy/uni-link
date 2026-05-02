package dev.ngb.app.profile.integration;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.support.IdentityAuthApiClient;
import dev.ngb.app.identity.support.TestOtpSender;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.update_profile.dto.UpdateProfileRequest;
import dev.ngb.app.profile.support.ProfileApiClient;
import dev.ngb.app.support.AbstractIntegrationTest;
import dev.ngb.app.support.HttpJsonClient;
import dev.ngb.app.support.TestUtils;
import dev.ngb.domain.identity.model.auth.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Profile API (core)")
class ProfileIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestOtpSender testOtpSender;

    private IdentityAuthApiClient identityAuth;
    private ProfileApiClient profiles;

    @BeforeEach
    void setUp() {
        testOtpSender.clear();
        var json = new HttpJsonClient(baseUrl(), restTemplate, objectMapper);
        identityAuth = new IdentityAuthApiClient(json);
        profiles = new ProfileApiClient(json, objectMapper);
    }

    @Test
    @DisplayName("POST /profiles without token -> 401")
    void createProfileWithoutAuthReturnsUnauthorized() {
        var result = profiles.createProfile(
                new CreateProfileRequest("guest.user", "Guest User", null, null)
        );
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    @DisplayName("Second POST /profiles for same account -> 409 PROFILE_ALREADY_EXISTS_FOR_ACCOUNT")
    void createProfileSecondTimeReturnsConflict() {
        var accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        var first = profiles.createProfile(
                new CreateProfileRequest("single.owner", "Single Owner", "bio one", null),
                headers
        );
        assertThat(first.isRight()).isTrue();

        var second = profiles.createProfile(
                new CreateProfileRequest("another.name", "Another Name", "bio two", null),
                headers
        );
        assertThat(second.isLeft()).isTrue();
        assertThat(second.getLeft().error()).isEqualTo("PROFILE_ALREADY_EXISTS_FOR_ACCOUNT");
    }

    @Test
    @DisplayName("GraphQL myProfile returns created profile")
    void getMyProfileReturnsSummary() {
        var accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        assertThat(profiles.createProfile(
                new CreateProfileRequest("me.user", "Me User", "hello", null),
                headers
        ).isRight()).isTrue();

        var me = profiles.queryMyProfile(headers);
        assertThat(me.isRight()).isTrue();
        assertThat(me.get().username()).isEqualTo("me.user");
        assertThat(me.get().displayName()).isEqualTo("Me User");
        assertThat(me.get().bio()).isEqualTo("hello");
    }

    @Test
    @DisplayName("PATCH /profiles/me updates profile fields")
    void patchProfileUpdatesFields() {
        var accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        assertThat(profiles.createProfile(
                new CreateProfileRequest("patch.user", "Patch User", null, null),
                headers
        ).isRight()).isTrue();

        var updated = profiles.updateProfile(
                new UpdateProfileRequest("New Display", "updated bio", "https://site.example", "Moon"),
                headers
        );
        assertThat(updated.isRight()).isTrue();
        assertThat(updated.get().displayName()).isEqualTo("New Display");
        assertThat(updated.get().bio()).isEqualTo("updated bio");
        assertThat(updated.get().website()).isEqualTo("https://site.example");
        assertThat(updated.get().location()).isEqualTo("Moon");
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

        var otherToken = registerAndVerifyToGetAccessToken();
        var otherHeaders = bearerHeaders(otherToken);

        var duplicate = profiles.createProfile(
                new CreateProfileRequest("taken.profile", "Another Display Name", null, null),
                otherHeaders
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
