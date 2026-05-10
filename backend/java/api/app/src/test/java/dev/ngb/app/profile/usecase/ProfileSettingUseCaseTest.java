package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.query.ProfileQueryService;
import dev.ngb.app.profile.application.usecase.profile.update_profile_setting.UpdateProfileSettingUseCase;
import dev.ngb.app.profile.application.usecase.profile.update_profile_setting.dto.UpdateProfileSettingRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Profile Setting use cases")
class ProfileSettingUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private ProfileLinkRepository profileLinkRepository;
    @Mock private ProfileMetadataRepository profileMetadataRepository;
    @Mock private ProfileSettingRepository profileSettingRepository;
    @Mock private FollowRequestRepository followRequestRepository;

    private ProfileQueryService profileQueryService;
    private UpdateProfileSettingUseCase updateProfileSettingUseCase;

    @BeforeEach
    void setUp() {
        profileQueryService = new ProfileQueryService(
                profileRepository,
                profileStatsRepository,
                profileRelationshipRepository,
                profileLinkRepository,
                profileMetadataRepository,
                profileSettingRepository,
                followRequestRepository
        );
        updateProfileSettingUseCase = new UpdateProfileSettingUseCase(profileRepository, profileSettingRepository);
    }

    @Test
    @DisplayName("Get: existing setting -> returned as-is")
    void getWhenExists() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileSettingRepository.findByProfileId(1L))
                .thenReturn(Optional.of(ProfileFixtures.defaultSetting(10L, 1L)));
        var resp = profileQueryService.getSettings(100L);
        assertThat(resp.allowMessages()).isTrue();
    }

    @Test
    @DisplayName("Get: missing setting -> default created and returned")
    void getWhenMissingBootstraps() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileSettingRepository.findByProfileId(1L)).thenReturn(Optional.empty());
        when(profileSettingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var resp = profileQueryService.getSettings(100L);
        assertThat(resp.allowMentions()).isTrue();
        verify(profileSettingRepository).save(any());
    }

    @Test
    @DisplayName("Get: profile missing -> PROFILE_NOT_FOUND")
    void getWhenNoProfile() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> profileQueryService.getSettings(100L));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Update: partial -> only provided fields change")
    void updateWhenPartial() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var existing = ProfileFixtures.defaultSetting(10L, 1L);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileSettingRepository.findByProfileId(1L)).thenReturn(Optional.of(existing));
        when(profileSettingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        updateProfileSettingUseCase.execute(100L, new UpdateProfileSettingRequest(false, null, null, null));

        var captor = ArgumentCaptor.forClass(ProfileSetting.class);
        verify(profileSettingRepository).save(captor.capture());
        assertThat(captor.getValue().getAllowMentions()).isFalse();
        assertThat(captor.getValue().getAllowMessages()).isTrue();
    }

    @Test
    @DisplayName("Update: profile missing -> PROFILE_NOT_FOUND")
    void updateWhenNoProfile() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> updateProfileSettingUseCase.execute(100L, new UpdateProfileSettingRequest(true, true, true, true)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
