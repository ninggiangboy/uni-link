package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.ProfileFollowStatsDeltaPublisher;
import dev.ngb.app.profile.application.usecase.remove_follower.RemoveFollowerUseCase;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveFollowerUseCase")
class RemoveFollowerUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileFollowStatsDeltaPublisher profileFollowStatsDeltaPublisher;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @InjectMocks private RemoveFollowerUseCase useCase;

    @Test
    @DisplayName("Existing follower -> removed and delta published")
    void executeWhenExisting() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        var follower = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(follower));
        when(profileRelationshipRepository.unfollow(2L, 1L)).thenReturn(true);

        useCase.execute(100L, "bob");

        verify(profileFollowStatsDeltaPublisher).publish(1L, -1, 2L, -1);
    }

    @Test
    @DisplayName("No such follower -> NOT_FOLLOWED_BY")
    void executeWhenMissing() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        var notAFollower = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(notAFollower));
        when(profileRelationshipRepository.unfollow(2L, 1L)).thenReturn(false);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.NOT_FOLLOWED_BY);
    }
}
