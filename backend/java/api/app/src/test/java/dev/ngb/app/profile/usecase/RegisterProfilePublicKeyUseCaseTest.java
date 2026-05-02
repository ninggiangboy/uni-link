package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.register_public_key.RegisterProfilePublicKeyUseCase;
import dev.ngb.app.profile.application.usecase.register_public_key.dto.RegisterProfilePublicKeyRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
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
@DisplayName("RegisterProfilePublicKeyUseCase")
class RegisterProfilePublicKeyUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @InjectMocks private RegisterProfilePublicKeyUseCase useCase;

    @Test
    @DisplayName("Valid key -> stored verbatim")
    void executeWhenValidStores() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, new RegisterProfilePublicKeyRequest("base64-pubkey"));

        var captor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(captor.capture());
        assertThat(captor.getValue().getPublicKey()).isEqualTo("base64-pubkey");
    }

    @Test
    @DisplayName("Profile missing -> PROFILE_NOT_FOUND")
    void executeWhenNoProfileThrowsNotFound() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new RegisterProfilePublicKeyRequest("k")));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
