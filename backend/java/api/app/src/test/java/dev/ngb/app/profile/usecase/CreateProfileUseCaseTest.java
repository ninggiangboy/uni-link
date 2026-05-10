package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.profile.create_profile.CreateProfileUseCase;
import dev.ngb.app.profile.application.usecase.profile.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.shared.public_api.IdentityPublicApi;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.model.username.ProfileUsername;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import dev.ngb.domain.profile.repository.ProfileUsernameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProfileUseCase")
class CreateProfileUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private ProfileSettingRepository profileSettingRepository;
    @Mock private ProfileUsernameRepository profileUsernameRepository;
    @Mock private IdentityPublicApi identityPublicApi;

    @InjectMocks
    private CreateProfileUseCase useCase;

    @Test
    @DisplayName("Inactive account -> ACCOUNT_NOT_ACTIVE")
    void executeWhenAccountNotActiveThrowsForbidden() {
        var accountId = 10L;
        var request = new CreateProfileRequest("user.one", "User One", "bio", ProfileVisibility.PUBLIC);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(false);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(accountId, request));

        assertThat(ex.getError()).isEqualTo(ProfileError.ACCOUNT_NOT_ACTIVE);
        verifyNoInteractions(profileRepository, profileStatsRepository, profileSettingRepository, profileUsernameRepository);
    }

    @Test
    @DisplayName("Account already owns a profile -> PROFILE_ALREADY_EXISTS_FOR_ACCOUNT")
    void executeWhenAccountAlreadyHasProfileThrowsConflict() {
        var accountId = 11L;
        var request = new CreateProfileRequest("user.two", "User Two", null, ProfileVisibility.PUBLIC);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(true);
        when(profileRepository.existsByAccountId(accountId)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(accountId, request));

        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_ALREADY_EXISTS_FOR_ACCOUNT);
        verify(profileRepository, never()).save(any(Profile.class));
        verifyNoInteractions(profileStatsRepository, profileSettingRepository, profileUsernameRepository);
    }

    @Test
    @DisplayName("Duplicate username -> USERNAME_ALREADY_EXISTS")
    void executeWhenUsernameExistsThrowsConflict() {
        var accountId = 12L;
        var request = new CreateProfileRequest("user.one", "User One", "bio", ProfileVisibility.PUBLIC);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(true);
        when(profileRepository.existsByAccountId(accountId)).thenReturn(false);
        when(profileRepository.existsByUsername("user.one")).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(accountId, request));

        assertThat(ex.getError()).isEqualTo(ProfileError.USERNAME_ALREADY_EXISTS);
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("Active account + available username -> profile + satellite rows persisted")
    void executeWhenValidCreatesProfileAndBootstrapsSatellites() {
        var accountId = 13L;
        var request = new CreateProfileRequest("user.one", "User One", "bio", ProfileVisibility.PRIVATE);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(true);
        when(profileRepository.existsByAccountId(accountId)).thenReturn(false);
        when(profileRepository.existsByUsername("user.one")).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> {
            Profile p = invocation.getArgument(0);
            return Profile.reconstruct(
                    200L,
                    "profile-uuid-200",
                    null,
                    Instant.now(),
                    null,
                    Instant.now(),
                    p.getAccountId(),
                    p.getUsername(),
                    p.getDisplayName(),
                    p.getBio(),
                    p.getWebsite(),
                    p.getLocation(),
                    p.getAvatarUrl(),
                    p.getBannerUrl(),
                    p.getVisibility(),
                    p.getIsVerified(),
                    p.getIsCeleb(),
                    p.getPublicKey()
            );
        });

        var response = useCase.execute(accountId, request);

        assertThat(response.profileUuid()).isEqualTo("profile-uuid-200");
        assertThat(response.username()).isEqualTo("user.one");
        assertThat(response.displayName()).isEqualTo("User One");
        assertThat(response.visibility()).isEqualTo(ProfileVisibility.PRIVATE);

        var savedProfile = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(savedProfile.capture());
        assertThat(savedProfile.getValue().getAccountId()).isEqualTo(accountId);

        var statsCaptor = ArgumentCaptor.forClass(ProfileStats.class);
        verify(profileStatsRepository).save(statsCaptor.capture());
        assertThat(statsCaptor.getValue().getProfileId()).isEqualTo(200L);
        assertThat(statsCaptor.getValue().getFollowerCount()).isZero();

        var settingCaptor = ArgumentCaptor.forClass(ProfileSetting.class);
        verify(profileSettingRepository).save(settingCaptor.capture());
        assertThat(settingCaptor.getValue().getProfileId()).isEqualTo(200L);
        assertThat(settingCaptor.getValue().getAllowMentions()).isTrue();

        var usernameCaptor = ArgumentCaptor.forClass(ProfileUsername.class);
        verify(profileUsernameRepository).save(usernameCaptor.capture());
        assertThat(usernameCaptor.getValue().getProfileId()).isEqualTo(200L);
        assertThat(usernameCaptor.getValue().getIsCurrent()).isTrue();
        assertThat(usernameCaptor.getValue().getUsername()).isEqualTo("user.one");
    }
}
