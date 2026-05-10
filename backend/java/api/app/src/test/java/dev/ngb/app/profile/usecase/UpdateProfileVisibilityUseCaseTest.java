package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.profile.update_profile_visibility.UpdateProfileVisibilityUseCase;
import dev.ngb.app.profile.application.usecase.profile.update_profile_visibility.dto.UpdateProfileVisibilityRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.domain.profile.repository.ProfileRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProfileVisibilityUseCase")
class UpdateProfileVisibilityUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @InjectMocks private UpdateProfileVisibilityUseCase useCase;

    @Test
    @DisplayName("Valid visibility -> persisted")
    void executeWhenValidUpdates() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, new UpdateProfileVisibilityRequest(ProfileVisibility.HIDDEN));

        var captor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(captor.capture());
        assertThat(captor.getValue().getVisibility()).isEqualTo(ProfileVisibility.HIDDEN);
    }

    @Test
    @DisplayName("Profile missing -> PROFILE_NOT_FOUND")
    void executeWhenNoProfileThrowsNotFound() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new UpdateProfileVisibilityRequest(ProfileVisibility.PRIVATE)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
