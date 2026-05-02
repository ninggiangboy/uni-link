package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.usecase.unfollow_profile.UnfollowProfileUseCase;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnfollowProfileUseCase")
class UnfollowProfileUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private FollowRequestRepository followRequestRepository;
    @InjectMocks private UnfollowProfileUseCase useCase;

    @Test
    @DisplayName("Edge exists -> deleted and stats decremented")
    void executeWhenEdgeExists() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.unfollow(1L, 2L)).thenReturn(true);
        when(profileStatsRepository.findByProfileId(2L)).thenReturn(Optional.of(ProfileFixtures.stats(20L, 2L, 5, 0)));
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.of(ProfileFixtures.stats(10L, 1L, 0, 5)));
        when(profileStatsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, "bob");

        verify(profileStatsRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Pending request -> cancelled")
    void executeWhenPendingRequestCancels() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        var pending = ProfileFixtures.pendingRequest(99L, 1L, 2L);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.unfollow(1L, 2L)).thenReturn(false);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.of(pending));
        when(followRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(100L, "bob");

        var captor = ArgumentCaptor.forClass(FollowRequest.class);
        verify(followRequestRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus().name()).isEqualTo("CANCELLED");
        verify(profileStatsRepository, never()).save(any());
    }

    @Test
    @DisplayName("No edge nor request -> NOT_FOLLOWING")
    void executeWhenNoneThrows() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.unfollow(1L, 2L)).thenReturn(false);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.NOT_FOLLOWING);
    }
}
