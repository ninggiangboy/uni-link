package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.update_profile_avatar.UpdateProfileAvatarUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_avatar.dto.UpdateProfileAvatarRequest;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.app.shared.public_api.AttachmentPublicApi;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileMedia;
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
@DisplayName("UpdateProfileAvatarUseCase")
class UpdateProfileAvatarUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileMediaRepository profileMediaRepository;
    @Mock private AttachmentPublicApi attachmentPublicApi;
    @InjectMocks private UpdateProfileAvatarUseCase useCase;

    @Test
    @DisplayName("Valid attachment -> avatar URL stored on profile + media row created")
    void executeWhenValidAttachmentSets() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(attachmentPublicApi.resolveAvailableUrl("att-1", 100L)).thenReturn(Optional.of("https://cdn/file.png"));
        when(profileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, new UpdateProfileAvatarRequest("att-1"));

        var profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        assertThat(profileCaptor.getValue().getAvatarUrl()).isEqualTo("https://cdn/file.png");

        var mediaCaptor = ArgumentCaptor.forClass(ProfileMedia.class);
        verify(profileMediaRepository).save(mediaCaptor.capture());
        assertThat(mediaCaptor.getValue().getUrl()).isEqualTo("https://cdn/file.png");
    }

    @Test
    @DisplayName("Attachment unknown or unowned -> INVALID_ATTACHMENT")
    void executeWhenInvalidAttachmentThrows() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(attachmentPublicApi.resolveAvailableUrl("att-bad", 100L)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new UpdateProfileAvatarRequest("att-bad")));
        assertThat(ex.getError()).isEqualTo(ProfileError.INVALID_ATTACHMENT);
    }

    @Test
    @DisplayName("Profile missing -> PROFILE_NOT_FOUND")
    void executeWhenNoProfileThrowsNotFound() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> useCase.execute(100L, new UpdateProfileAvatarRequest("att-1")));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
