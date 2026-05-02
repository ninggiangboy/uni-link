package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.ProfileFollowStatsDeltaPublisher;
import dev.ngb.app.profile.application.dto.FollowResponse;
import dev.ngb.app.profile.application.usecase.follow_profile.FollowProfileUseCase;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowProfileUseCase")
class FollowProfileUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileFollowStatsDeltaPublisher profileFollowStatsDeltaPublisher;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private FollowRequestRepository followRequestRepository;
    @InjectMocks private FollowProfileUseCase useCase;

    @Test
    @DisplayName("Public target + new edge -> delta published and FOLLOWING returned")
    void executeWhenPublicCreatesEdge() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(2L, 1L)).thenReturn(false);
        when(profileRelationshipRepository.isBlocked(1L, 2L)).thenReturn(false);
        when(profileRelationshipRepository.follow(eq(1L), eq(2L), any(Instant.class))).thenReturn(true);

        var resp = useCase.execute(100L, "bob");

        assertThat(resp.status()).isEqualTo(FollowResponse.Status.FOLLOWING);
        verify(profileFollowStatsDeltaPublisher).publish(2L, 1, 1L, 1);
    }

    @Test
    @DisplayName("Public target + edge already exists -> ALREADY_FOLLOWING")
    void executeWhenAlreadyFollowingThrows() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(anyLong(), anyLong())).thenReturn(false);
        when(profileRelationshipRepository.follow(eq(1L), eq(2L), any(Instant.class))).thenReturn(false);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.ALREADY_FOLLOWING);
        verify(profileFollowStatsDeltaPublisher, never()).publish(anyLong(), anyInt(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("Self-follow -> CANNOT_FOLLOW_SELF")
    void executeWhenSelf() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "alice"));
        assertThat(ex.getError()).isEqualTo(ProfileError.CANNOT_FOLLOW_SELF);
    }

    @Test
    @DisplayName("Hidden target -> 404 (PROFILE_NOT_FOUND)")
    void executeWhenHidden() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob", ProfileVisibility.HIDDEN);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Blocked by target -> BLOCKED_BY_TARGET")
    void executeWhenBlockedByTarget() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(2L, 1L)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.BLOCKED_BY_TARGET);
    }

    @Test
    @DisplayName("Blocked target -> TARGET_BLOCKED")
    void executeWhenTargetBlocked() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(2L, 1L)).thenReturn(false);
        when(profileRelationshipRepository.isBlocked(1L, 2L)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.TARGET_BLOCKED);
    }

    @Test
    @DisplayName("Private target -> creates pending FollowRequest")
    void executeWhenPrivateCreatesRequest() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob", ProfileVisibility.PRIVATE);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(anyLong(), anyLong())).thenReturn(false);
        when(profileRelationshipRepository.isFollowing(1L, 2L)).thenReturn(false);
        when(followRequestRepository.existsPending(1L, 2L)).thenReturn(false);
        when(followRequestRepository.save(any())).thenAnswer(inv -> ProfileFixtures.pendingRequest(99L, 1L, 2L));

        var resp = useCase.execute(100L, "bob");

        assertThat(resp.status()).isEqualTo(FollowResponse.Status.REQUESTED);
        assertThat(resp.followRequestUuid()).isEqualTo("req-99");
        verify(profileFollowStatsDeltaPublisher, never()).publish(anyLong(), anyInt(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("Private target + already following -> ALREADY_FOLLOWING")
    void executeWhenPrivateAlreadyFollowing() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob", ProfileVisibility.PRIVATE);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(anyLong(), anyLong())).thenReturn(false);
        when(profileRelationshipRepository.isFollowing(1L, 2L)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.ALREADY_FOLLOWING);
    }

    @Test
    @DisplayName("Private target + already requested -> FOLLOW_REQUEST_ALREADY_PENDING")
    void executeWhenPrivateAlreadyPending() {
        var follower = ProfileFixtures.profile(1L, 100L, "alice");
        var target = ProfileFixtures.profile(2L, 200L, "bob", ProfileVisibility.PRIVATE);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(follower));
        when(profileRepository.findByUsername("bob")).thenReturn(Optional.of(target));
        when(profileRelationshipRepository.isBlocked(anyLong(), anyLong())).thenReturn(false);
        when(profileRelationshipRepository.isFollowing(1L, 2L)).thenReturn(false);
        when(followRequestRepository.existsPending(1L, 2L)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.FOLLOW_REQUEST_ALREADY_PENDING);
    }

    @Test
    @DisplayName("Follower profile missing -> PROFILE_NOT_FOUND")
    void executeWhenFollowerMissing() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> useCase.execute(100L, "bob"));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

}
