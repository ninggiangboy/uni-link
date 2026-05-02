package dev.ngb.app.profile.integration;

import dev.ngb.app.identity.application.dto.DeviceInfo;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.support.IdentityAuthApiClient;
import dev.ngb.app.identity.support.TestOtpSender;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.update_profile_setting.dto.UpdateProfileSettingRequest;
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

@DisplayName("Profile settings API")
class ProfileSettingsIntegrationTest extends AbstractIntegrationTest {

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
    @DisplayName("GraphQL mySettings + PATCH /profiles/me/settings")
    void getAndUpdateSettings() {
        var accessToken = registerAndVerifyToGetAccessToken();
        var headers = bearerHeaders(accessToken);

        assertThat(profiles.createProfile(
                new CreateProfileRequest("settings.user", "Settings User", null, null),
                headers
        ).isRight()).isTrue();

        var get = profiles.queryMySettings(headers);
        assertThat(get.isRight()).isTrue();
        assertThat(get.get().allowMentions()).isTrue();

        var patch = profiles.updateSettings(
                new UpdateProfileSettingRequest(false, true, null, null),
                headers
        );
        assertThat(patch.isRight()).isTrue();
        assertThat(patch.get().allowMentions()).isFalse();
        assertThat(patch.get().allowMessages()).isTrue();
    }

    private String registerAndVerifyToGetAccessToken() {
        var email = TestUtils.getUniqueEmail();
        var password = "Password1!";
        var device = new DeviceInfo(DeviceType.WEB, "settings-test-browser", "settings-fp-" + System.nanoTime());

        var register = identityAuth.createAccount(new CreateAccountRequest(email, password));
        assertThat(register.isRight()).isTrue();
        var verificationId = register.get().verificationId();

        var otp = testOtpSender.getLastOtpCode().orElseThrow();
        var verify = identityAuth.completeEmailVerification(verificationId, new CompleteEmailVerificationRequest(otp, device));
        assertThat(verify.isRight()).isTrue();
        return verify.get().accessToken();
    }
}
