package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.service.FollowStatsSyncService;
import dev.ngb.app.profile.application.usecase.social.block_profile.BlockProfileUseCase;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.model.relationship.ProfileRelationshipState;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlockProfileUseCase")
class BlockProfileUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private FollowStatsSyncService followStatsSyncService;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private FollowRequestRepository followRequestRepository;

    @InjectMocks
    private BlockProfileUseCase useCase;

    private final Profile blocker = ProfileFixtures.profile(1L, 100L, "blocker");
    private final Profile target = ProfileFixtures.profile(2L, 200L, "target");

    @Test
    @DisplayName("Blocker not found → PROFILE_NOT_FOUND")
    void executeWhenBlockerNotFoundThrows() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "target"));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Target not found → PROFILE_NOT_FOUND")
    void executeWhenTargetNotFoundThrows() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "target"));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Block self → CANNOT_BLOCK_SELF")
    void executeWhenBlockSelfThrows() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("blocker")).thenReturn(Optional.of(blocker));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "blocker"));
        assertThat(ex.getError()).isEqualTo(ProfileError.CANNOT_BLOCK_SELF);
        verifyNoInteractions(profileRelationshipRepository, followStatsSyncService);
    }

    @Test
    @DisplayName("Already blocked → ALREADY_BLOCKED")
    void executeWhenAlreadyBlockedThrows() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.findRelationshipsBetween(1L, 2L))
                .thenReturn(new ProfileRelationshipState(false, false, false, false, false, false));
        when(profileRelationshipRepository.blockAndCleanupFollows(1L, 2L)).thenReturn(false);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "target"));
        assertThat(ex.getError()).isEqualTo(ProfileError.ALREADY_BLOCKED);
        verifyNoInteractions(followStatsSyncService);
    }

    @Test
    @DisplayName("No prior relationship → block created, no stats adjustment")
    void executeWhenNoPriorRelationship() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.of(target));
        var relState = ProfileRelationshipState.empty();
        when(profileRelationshipRepository.findRelationshipsBetween(1L, 2L)).thenReturn(relState);
        when(profileRelationshipRepository.blockAndCleanupFollows(1L, 2L)).thenReturn(true);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.empty());
        when(followRequestRepository.findPendingForPair(2L, 1L)).thenReturn(Optional.empty());

        useCase.execute(100L, "target");

        verifyNoInteractions(followStatsSyncService);
    }

    @Test
    @DisplayName("Mutual follows → cleanUpFollow called")
    void executeWhenMutualFollowsCleanedUp() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.of(target));
        var relState = new ProfileRelationshipState(true, true, false, false, false, false);
        when(profileRelationshipRepository.findRelationshipsBetween(1L, 2L)).thenReturn(relState);
        when(profileRelationshipRepository.blockAndCleanupFollows(1L, 2L)).thenReturn(true);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.empty());
        when(followRequestRepository.findPendingForPair(2L, 1L)).thenReturn(Optional.empty());

        useCase.execute(100L, "target");

        verify(followStatsSyncService).cleanUpFollow(target, blocker);
    }

    @Test
    @DisplayName("Source follows target → unfollow called")
    void executeWhenSourceFollowsTarget() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.of(target));
        var relState = new ProfileRelationshipState(true, false, false, false, false, false);
        when(profileRelationshipRepository.findRelationshipsBetween(1L, 2L)).thenReturn(relState);
        when(profileRelationshipRepository.blockAndCleanupFollows(1L, 2L)).thenReturn(true);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.empty());
        when(followRequestRepository.findPendingForPair(2L, 1L)).thenReturn(Optional.empty());

        useCase.execute(100L, "target");

        verify(followStatsSyncService).unfollow(target, blocker);
        verify(followStatsSyncService, never()).unfollow(blocker, target);
        verify(followStatsSyncService, never()).cleanUpFollow(any(), any());
    }

    @Test
    @DisplayName("Target follows source → unfollow called")
    void executeWhenTargetFollowsSource() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.of(target));
        var relState = new ProfileRelationshipState(false, true, false, false, false, false);
        when(profileRelationshipRepository.findRelationshipsBetween(1L, 2L)).thenReturn(relState);
        when(profileRelationshipRepository.blockAndCleanupFollows(1L, 2L)).thenReturn(true);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.empty());
        when(followRequestRepository.findPendingForPair(2L, 1L)).thenReturn(Optional.empty());

        useCase.execute(100L, "target");

        verify(followStatsSyncService).unfollow(blocker, target);
        verify(followStatsSyncService, never()).unfollow(target, blocker);
        verify(followStatsSyncService, never()).cleanUpFollow(any(), any());
    }

    @Test
    @DisplayName("Pending follow requests cancelled on block")
    void executeWhenCancelsPendingFollowRequests() {
        var blockerRequest = ProfileFixtures.pendingRequest(10L, 1L, 2L);
        var targetRequest = ProfileFixtures.pendingRequest(11L, 2L, 1L);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(blocker));
        when(profileRepository.findByUsername("target")).thenReturn(Optional.of(target));
        var relState = ProfileRelationshipState.empty();
        when(profileRelationshipRepository.findRelationshipsBetween(1L, 2L)).thenReturn(relState);
        when(profileRelationshipRepository.blockAndCleanupFollows(1L, 2L)).thenReturn(true);
        when(followRequestRepository.findPendingForPair(1L, 2L)).thenReturn(Optional.of(blockerRequest));
        when(followRequestRepository.findPendingForPair(2L, 1L)).thenReturn(Optional.of(targetRequest));

        useCase.execute(100L, "target");

        var captor = ArgumentCaptor.forClass(FollowRequest.class);
        verify(followRequestRepository, times(2)).save(captor.capture());
        var saved = captor.getAllValues();
        assertThat(saved.get(0).getStatus().name()).isEqualTo("CANCELLED");
        assertThat(saved.get(1).getStatus().name()).isEqualTo("CANCELLED");
    }
}
