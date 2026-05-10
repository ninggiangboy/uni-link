package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.profile.update_profile_banner.UpdateProfileBannerUseCase;
import dev.ngb.app.profile.application.usecase.profile.update_profile_banner.dto.UpdateProfileBannerRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.app.shared.public_api.AttachmentPublicApi;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileMediaRepository;
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
@DisplayName("UpdateProfileBannerUseCase")
class UpdateProfileBannerUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileMediaRepository profileMediaRepository;
    @Mock private AttachmentPublicApi attachmentPublicApi;
    @InjectMocks private UpdateProfileBannerUseCase useCase;

    @Test
    @DisplayName("Valid attachment -> banner URL stored")
    void executeWhenValidAttachmentSets() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(attachmentPublicApi.resolveAvailableUrl("att-1", 100L)).thenReturn(Optional.of("https://cdn/banner.png"));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, new UpdateProfileBannerRequest("att-1"));

        var profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        assertThat(profileCaptor.getValue().getBannerUrl()).isEqualTo("https://cdn/banner.png");
    }

    @Test
    @DisplayName("Invalid attachment -> INVALID_ATTACHMENT")
    void executeWhenInvalidAttachmentThrows() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(attachmentPublicApi.resolveAvailableUrl("att-bad", 100L)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new UpdateProfileBannerRequest("att-bad")));
        assertThat(ex.getError()).isEqualTo(ProfileError.INVALID_ATTACHMENT);
    }

    @Test
    @DisplayName("Profile missing -> PROFILE_NOT_FOUND")
    void executeWhenNoProfileThrowsNotFound() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new UpdateProfileBannerRequest("att-1")));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
