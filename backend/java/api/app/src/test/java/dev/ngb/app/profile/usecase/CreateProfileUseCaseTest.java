package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.create_profile.CreateProfileUseCase;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.shared.public_api.IdentityPublicApi;
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

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProfileUseCase")
class CreateProfileUseCaseTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private IdentityPublicApi identityPublicApi;

    @InjectMocks
    private CreateProfileUseCase useCase;

    @Test
    @DisplayName("Inactive account -> ACCOUNT_NOT_ACTIVE")
    void executeWhenAccountNotActiveThrowsForbidden() {
        Long accountId = 10L;
        CreateProfileRequest request = new CreateProfileRequest("user.one", "User One", "bio", ProfileVisibility.PUBLIC);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(false);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(accountId, request));

        assertThat(ex.getError()).isEqualTo(ProfileError.ACCOUNT_NOT_ACTIVE);
        verifyNoInteractions(profileRepository);
    }

    @Test
    @DisplayName("Duplicate username -> USERNAME_ALREADY_EXISTS")
    void executeWhenUsernameExistsThrowsConflict() {
        Long accountId = 11L;
        CreateProfileRequest request = new CreateProfileRequest("user.one", "User One", "bio", ProfileVisibility.PUBLIC);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(true);
        when(profileRepository.existsByUsername("user.one")).thenReturn(true);

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(accountId, request));

        assertThat(ex.getError()).isEqualTo(ProfileError.USERNAME_ALREADY_EXISTS);
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    @DisplayName("Active account + available username -> profile created")
    void executeWhenValidCreatesProfile() {
        Long accountId = 12L;
        CreateProfileRequest request = new CreateProfileRequest("user.one", "User One", "bio", ProfileVisibility.PRIVATE);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(true);
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
                    p.getPublicKey()
            );
        });

        CreateProfileResponse response = useCase.execute(accountId, request);

        assertThat(response.profileUuid()).isEqualTo("profile-uuid-200");
        assertThat(response.username()).isEqualTo("user.one");
        assertThat(response.displayName()).isEqualTo("User One");
        assertThat(response.visibility()).isEqualTo(ProfileVisibility.PRIVATE);

        ArgumentCaptor<Profile> savedCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(savedCaptor.capture());
        assertThat(savedCaptor.getValue().getAccountId()).isEqualTo(accountId);
    }

    @Test
    @DisplayName("Same active account can create multiple profiles with different usernames")
    void executeWhenSameAccountCreatesDifferentUsernamesSucceeds() {
        Long accountId = 13L;
        CreateProfileRequest requestOne = new CreateProfileRequest("user.one", "User One", null, ProfileVisibility.PUBLIC);
        CreateProfileRequest requestTwo = new CreateProfileRequest("user.two", "User Two", null, ProfileVisibility.PUBLIC);
        when(identityPublicApi.isAccountActive(accountId)).thenReturn(true);
        when(profileRepository.existsByUsername("user.one")).thenReturn(false);
        when(profileRepository.existsByUsername("user.two")).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateProfileResponse first = useCase.execute(accountId, requestOne);
        CreateProfileResponse second = useCase.execute(accountId, requestTwo);

        assertThat(first.username()).isEqualTo("user.one");
        assertThat(second.username()).isEqualTo("user.two");
        verify(profileRepository, times(2)).save(any(Profile.class));
    }
}
