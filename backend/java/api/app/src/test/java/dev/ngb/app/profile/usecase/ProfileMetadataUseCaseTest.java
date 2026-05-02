package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.ProfileQueryService;
import dev.ngb.app.profile.application.usecase.remove_profile_metadata.RemoveProfileMetadataUseCase;
import dev.ngb.app.profile.application.usecase.upsert_profile_metadata.UpsertProfileMetadataUseCase;
import dev.ngb.app.profile.application.usecase.upsert_profile_metadata.dto.UpsertProfileMetadataRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.ProfileMetadata;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Profile Metadata use cases")
class ProfileMetadataUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private ProfileLinkRepository profileLinkRepository;
    @Mock private ProfileMetadataRepository profileMetadataRepository;
    @Mock private ProfileSettingRepository profileSettingRepository;
    @Mock private FollowRequestRepository followRequestRepository;

    private ProfileQueryService profileQueryService;
    private UpsertProfileMetadataUseCase upsertProfileMetadataUseCase;
    private RemoveProfileMetadataUseCase removeProfileMetadataUseCase;

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
        upsertProfileMetadataUseCase = new UpsertProfileMetadataUseCase(profileRepository, profileMetadataRepository);
        removeProfileMetadataUseCase = new RemoveProfileMetadataUseCase(profileRepository, profileMetadataRepository);
    }

    @Test
    @DisplayName("List: returns all rows")
    void listReturns() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileMetadataRepository.findByProfileId(1L)).thenReturn(List.of(
                ProfileFixtures.metadata(10L, 1L, "pronouns", "they/them")
        ));
        var resp = profileQueryService.listMetadata(100L);
        assertThat(resp).hasSize(1);
        assertThat(resp.get(0).key()).isEqualTo("pronouns");
    }

    @Test
    @DisplayName("List: profile missing -> PROFILE_NOT_FOUND")
    void listWhenNoProfile() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> profileQueryService.listMetadata(100L));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Upsert: existing key -> updated")
    void upsertWhenExists() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var existing = ProfileFixtures.metadata(10L, 1L, "pronouns", "she/her");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileMetadataRepository.findByProfileIdAndKey(1L, "pronouns")).thenReturn(Optional.of(existing));
        when(profileMetadataRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        upsertProfileMetadataUseCase.execute(100L, "pronouns", new UpsertProfileMetadataRequest("they/them"));

        var captor = ArgumentCaptor.forClass(ProfileMetadata.class);
        verify(profileMetadataRepository).save(captor.capture());
        assertThat(captor.getValue().getValue()).isEqualTo("they/them");
    }

    @Test
    @DisplayName("Upsert: missing key -> created")
    void upsertWhenCreates() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileMetadataRepository.findByProfileIdAndKey(1L, "pronouns")).thenReturn(Optional.empty());
        when(profileMetadataRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        upsertProfileMetadataUseCase.execute(100L, "pronouns", new UpsertProfileMetadataRequest("she/her"));

        var captor = ArgumentCaptor.forClass(ProfileMetadata.class);
        verify(profileMetadataRepository).save(captor.capture());
        assertThat(captor.getValue().getKey()).isEqualTo("pronouns");
    }

    @Test
    @DisplayName("Upsert: invalid key -> validation error")
    void upsertWhenInvalidKey() {
        assertThrows(RuntimeException.class,
                () -> upsertProfileMetadataUseCase.execute(100L, "bad key!", new UpsertProfileMetadataRequest("v")));
    }

    @Test
    @DisplayName("Remove: existing -> deleted")
    void removeWhenExists() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var existing = ProfileFixtures.metadata(10L, 1L, "pronouns", "she/her");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileMetadataRepository.findByProfileIdAndKey(1L, "pronouns")).thenReturn(Optional.of(existing));

        removeProfileMetadataUseCase.execute(100L, "pronouns");
        verify(profileMetadataRepository).delete(existing);
    }

    @Test
    @DisplayName("Remove: missing -> METADATA_KEY_NOT_FOUND")
    void removeWhenMissing() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileMetadataRepository.findByProfileIdAndKey(1L, "missing")).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> removeProfileMetadataUseCase.execute(100L, "missing"));
        assertThat(ex.getError()).isEqualTo(ProfileError.METADATA_KEY_NOT_FOUND);
    }
}
