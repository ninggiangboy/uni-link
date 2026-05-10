package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.ProfileConstants;
import dev.ngb.app.profile.application.query.ProfileQueryService;
import dev.ngb.app.profile.application.usecase.link.add_profile_link.AddProfileLinkUseCase;
import dev.ngb.app.profile.application.usecase.link.add_profile_link.dto.AddProfileLinkRequest;
import dev.ngb.app.profile.application.usecase.link.remove_profile_link.RemoveProfileLinkUseCase;
import dev.ngb.app.profile.application.usecase.link.update_profile_link.UpdateProfileLinkUseCase;
import dev.ngb.app.profile.application.usecase.link.update_profile_link.dto.UpdateProfileLinkRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.ProfileLinkType;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Profile Link use cases")
class ProfileLinkUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private ProfileLinkRepository profileLinkRepository;
    @Mock private ProfileMetadataRepository profileMetadataRepository;
    @Mock private ProfileSettingRepository profileSettingRepository;
    @Mock private FollowRequestRepository followRequestRepository;

    private ProfileQueryService profileQueryService;
    private AddProfileLinkUseCase addProfileLinkUseCase;
    private UpdateProfileLinkUseCase updateProfileLinkUseCase;
    private RemoveProfileLinkUseCase removeProfileLinkUseCase;

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
        addProfileLinkUseCase = new AddProfileLinkUseCase(profileRepository, profileLinkRepository);
        updateProfileLinkUseCase = new UpdateProfileLinkUseCase(profileRepository, profileLinkRepository);
        removeProfileLinkUseCase = new RemoveProfileLinkUseCase(profileRepository, profileLinkRepository);
    }

    @Test
    @DisplayName("List: public profile -> returns links")
    void listWhenPublicReturns() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));
        when(profileLinkRepository.findByProfileId(1L)).thenReturn(List.of(
                ProfileFixtures.link(20L, 1L, "https://example.com")
        ));

        var links = profileQueryService.listProfileLinks("alice");
        assertThat(links).hasSize(1);
    }

    @Test
    @DisplayName("List: hidden profile -> 404")
    void listWhenHiddenThrows() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.HIDDEN);
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));

        var ex = assertThrows(DomainException.class, () -> profileQueryService.listProfileLinks("alice"));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("List: unknown username -> 404")
    void listWhenMissingThrows() {
        when(profileRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> profileQueryService.listProfileLinks("ghost"));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Add: below max -> persisted")
    void addWhenWithinLimit() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileLinkRepository.countByProfileId(1L)).thenReturn(2L);
        when(profileLinkRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new AddProfileLinkRequest(ProfileLinkType.OTHER, "https://example.com", 0);
        var resp = addProfileLinkUseCase.execute(100L, req);
        assertThat(resp.url()).isEqualTo("https://example.com");
    }

    @Test
    @DisplayName("Add: at max -> MAX_LINKS_REACHED")
    void addWhenAtMaxThrows() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileLinkRepository.countByProfileId(1L))
                .thenReturn((long) ProfileConstants.MAX_PROFILE_LINKS);

        var req = new AddProfileLinkRequest(ProfileLinkType.OTHER, "https://example.com", 0);
        var ex = assertThrows(DomainException.class, () -> addProfileLinkUseCase.execute(100L, req));
        assertThat(ex.getError()).isEqualTo(ProfileError.MAX_LINKS_REACHED);
        verify(profileLinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Add: profile missing -> PROFILE_NOT_FOUND")
    void addWhenNoProfile() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var req = new AddProfileLinkRequest(ProfileLinkType.OTHER, "https://example.com", 0);
        var ex = assertThrows(DomainException.class, () -> addProfileLinkUseCase.execute(100L, req));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Update: existing link -> updated")
    void updateWhenExists() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var link = ProfileFixtures.link(20L, 1L, "https://old.example");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileLinkRepository.findByUuidAndProfileId("link-20", 1L)).thenReturn(Optional.of(link));
        when(profileLinkRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new UpdateProfileLinkRequest(null, "https://new.example", 5);
        var resp = updateProfileLinkUseCase.execute(100L, "link-20", req);
        assertThat(resp.url()).isEqualTo("https://new.example");
    }

    @Test
    @DisplayName("Update: missing link -> LINK_NOT_FOUND")
    void updateWhenMissingLink() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileLinkRepository.findByUuidAndProfileId("link-x", 1L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> updateProfileLinkUseCase.execute(100L, "link-x", new UpdateProfileLinkRequest(null, "https://x", null)));
        assertThat(ex.getError()).isEqualTo(ProfileError.LINK_NOT_FOUND);
    }

    @Test
    @DisplayName("Remove: existing link -> deleted")
    void removeWhenExists() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var link = ProfileFixtures.link(20L, 1L, "https://x");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileLinkRepository.findByUuidAndProfileId("link-20", 1L)).thenReturn(Optional.of(link));

        removeProfileLinkUseCase.execute(100L, "link-20");
        verify(profileLinkRepository).delete(link);
    }

    @Test
    @DisplayName("Remove: missing link -> LINK_NOT_FOUND")
    void removeWhenMissingLink() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileLinkRepository.findByUuidAndProfileId("link-x", 1L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> removeProfileLinkUseCase.execute(100L, "link-x"));
        assertThat(ex.getError()).isEqualTo(ProfileError.LINK_NOT_FOUND);
    }
}
