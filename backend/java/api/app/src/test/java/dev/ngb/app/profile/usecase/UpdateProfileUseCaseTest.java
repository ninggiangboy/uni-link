package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.profile.update_profile.UpdateProfileUseCase;
import dev.ngb.app.profile.application.usecase.profile.update_profile.dto.UpdateProfileRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProfileUseCase")
class UpdateProfileUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @InjectMocks private UpdateProfileUseCase useCase;

    @Test
    @DisplayName("Valid update -> persisted and summary returned")
    void executeWhenValidUpdates() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.empty());

        var request = new UpdateProfileRequest("New Name", "New bio", "https://example.com", "Earth");
        var summary = useCase.execute(100L, request);

        assertThat(summary.displayName()).isEqualTo("New Name");
        assertThat(summary.bio()).isEqualTo("New bio");

        var captor = ArgumentCaptor.forClass(dev.ngb.domain.profile.model.profile.Profile.class);
        org.mockito.Mockito.verify(profileRepository).save(captor.capture());
        assertThat(captor.getValue().getWebsite()).isEqualTo("https://example.com");
        assertThat(captor.getValue().getLocation()).isEqualTo("Earth");
    }

    @Test
    @DisplayName("Profile missing -> PROFILE_NOT_FOUND")
    void executeWhenNoProfileThrowsNotFound() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var request = new UpdateProfileRequest("New Name", null, null, null);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, request));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
