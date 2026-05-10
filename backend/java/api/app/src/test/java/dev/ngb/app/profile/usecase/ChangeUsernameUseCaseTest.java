package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.profile.change_username.ChangeUsernameUseCase;
import dev.ngb.app.profile.application.usecase.profile.change_username.dto.ChangeUsernameRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.username.ProfileUsername;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileUsernameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeUsernameUseCase")
class ChangeUsernameUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileUsernameRepository profileUsernameRepository;
    @InjectMocks private ChangeUsernameUseCase useCase;

    @Test
    @DisplayName("Valid new username -> profile + username history updated")
    void executeWhenValidUpdates() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var prevCurrent = ProfileFixtures.currentUsername(50L, 1L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileUsernameRepository.findCurrentByProfileId(1L)).thenReturn(Optional.of(prevCurrent));
        when(profileUsernameRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, new ChangeUsernameRequest("alice2"));

        var profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        assertThat(profileCaptor.getValue().getUsername()).isEqualTo("alice2");

        ArgumentCaptor<ProfileUsername> unameCaptor = ArgumentCaptor.forClass(ProfileUsername.class);
        verify(profileUsernameRepository, times(2)).save(unameCaptor.capture());

        List<ProfileUsername> saved = unameCaptor.getAllValues();
        assertThat(saved.get(0).getId()).isEqualTo(50L);
        assertThat(saved.get(0).getIsCurrent()).isFalse();
        assertThat(saved.get(1).getId()).isNull();
        assertThat(saved.get(1).getIsCurrent()).isTrue();
        assertThat(saved.get(1).getUsername()).isEqualTo("alice2");
    }

    @Test
    @DisplayName("Same username -> USERNAME_UNCHANGED")
    void executeWhenSameUsernameThrows() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));

        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new ChangeUsernameRequest("alice")));
        assertThat(ex.getError()).isEqualTo(ProfileError.USERNAME_UNCHANGED);
        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Username taken on save → repository translates → USERNAME_ALREADY_EXISTS")
    void executeWhenUsernameTakenOnSaveThrowsConflict() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenThrow(ProfileError.USERNAME_ALREADY_EXISTS.exception());

        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new ChangeUsernameRequest("bob")));
        assertThat(ex.getError()).isEqualTo(ProfileError.USERNAME_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Profile missing -> PROFILE_NOT_FOUND")
    void executeWhenNoProfileThrows() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new ChangeUsernameRequest("alice2")));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
