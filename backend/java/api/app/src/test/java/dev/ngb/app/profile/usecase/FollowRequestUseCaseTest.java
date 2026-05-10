package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.service.FollowStatsSyncService;
import dev.ngb.app.profile.application.query.ProfileQueryService;
import dev.ngb.app.profile.application.query.dto.PageQuery;
import dev.ngb.app.profile.application.usecase.social.approve_follow_request.ApproveFollowRequestUseCase;
import dev.ngb.app.profile.application.usecase.social.reject_follow_request.RejectFollowRequestUseCase;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowRequest use cases")
class FollowRequestUseCaseTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private FollowStatsSyncService followStatsSyncService;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private FollowRequestRepository followRequestRepository;
    @Mock private ProfileLinkRepository profileLinkRepository;
    @Mock private ProfileMetadataRepository profileMetadataRepository;
    @Mock private ProfileSettingRepository profileSettingRepository;

    private ProfileQueryService profileQueryService;
    private ApproveFollowRequestUseCase approveFollowRequestUseCase;
    private RejectFollowRequestUseCase rejectFollowRequestUseCase;

    @BeforeEach
    void setUp() {
        profileQueryService = new ProfileQueryService(
                profileRepository,
                profileStatsRepository,
                profileRelationshipRepository,
                profileLinkRepository,
                profileMetadataRepository,
                profileSettingRepository,
                followRequestRepository
        );
        approveFollowRequestUseCase = new ApproveFollowRequestUseCase(
                profileRepository, followStatsSyncService, profileRelationshipRepository, followRequestRepository);
        rejectFollowRequestUseCase = new RejectFollowRequestUseCase(profileRepository, followRequestRepository);
    }

    @Test
    @DisplayName("List pending -> hydrated requester profiles")
    void listPendingReturns() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(followRequestRepository.findPendingByTargetProfileId(1L, 20, 0)).thenReturn(List.of(
                ProfileFixtures.pendingRequest(99L, 2L, 1L)
        ));
        when(profileRepository.findByIds(List.of(2L))).thenReturn(List.of(ProfileFixtures.profile(2L, 200L, "bob")));

        var resp = profileQueryService.listPendingFollowRequests(100L, PageQuery.of(20, 0));
        assertThat(resp).hasSize(1);
        assertThat(resp.get(0).requester().username()).isEqualTo("bob");
    }

    @Test
    @DisplayName("List pending: profile missing -> PROFILE_NOT_FOUND")
    void listPendingWhenNoProfile() {
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class,
                () -> profileQueryService.listPendingFollowRequests(100L, PageQuery.of(20, 0)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("Approve: pending -> approved + edge + stats")
    void approveWhenPending() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        var requester = ProfileFixtures.profile(2L, 200L, "bob");
        var request = ProfileFixtures.pendingRequest(99L, 2L, 1L);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(followRequestRepository.findByUuidAndTargetProfileId("req-99", 1L)).thenReturn(Optional.of(request));
        when(followRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(profileRelationshipRepository.follow(eq(2L), eq(1L))).thenReturn(true);
        when(profileRepository.findById(2L)).thenReturn(Optional.of(requester));

        approveFollowRequestUseCase.execute(100L, "req-99");

        var captor = ArgumentCaptor.forClass(FollowRequest.class);
        verify(followRequestRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus().name()).isEqualTo("APPROVED");
        verify(followStatsSyncService).follow(eq(owner), eq(requester));
    }

    @Test
    @DisplayName("Approve: already resolved -> FOLLOW_REQUEST_NOT_FOUND")
    void approveWhenResolved() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        var request = ProfileFixtures.pendingRequest(99L, 2L, 1L);
        request.approve();
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(followRequestRepository.findByUuidAndTargetProfileId("req-99", 1L)).thenReturn(Optional.of(request));

        var ex = assertThrows(DomainException.class, () -> approveFollowRequestUseCase.execute(100L, "req-99"));
        assertThat(ex.getError()).isEqualTo(ProfileError.FOLLOW_REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("Approve: missing request -> FOLLOW_REQUEST_NOT_FOUND")
    void approveWhenMissing() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(followRequestRepository.findByUuidAndTargetProfileId("req-x", 1L)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> approveFollowRequestUseCase.execute(100L, "req-x"));
        assertThat(ex.getError()).isEqualTo(ProfileError.FOLLOW_REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("Reject: pending -> rejected, no edge")
    void rejectWhenPending() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        var request = ProfileFixtures.pendingRequest(99L, 2L, 1L);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(followRequestRepository.findByUuidAndTargetProfileId("req-99", 1L)).thenReturn(Optional.of(request));
        when(followRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        rejectFollowRequestUseCase.execute(100L, "req-99");

        var captor = ArgumentCaptor.forClass(FollowRequest.class);
        verify(followRequestRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus().name()).isEqualTo("REJECTED");
        verify(profileRelationshipRepository, never()).follow(any(), any());
    }

    @Test
    @DisplayName("Reject: already resolved -> FOLLOW_REQUEST_NOT_FOUND")
    void rejectWhenResolved() {
        var owner = ProfileFixtures.profile(1L, 100L, "alice");
        var request = ProfileFixtures.pendingRequest(99L, 2L, 1L);
        request.reject();
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(owner));
        when(followRequestRepository.findByUuidAndTargetProfileId("req-99", 1L)).thenReturn(Optional.of(request));

        var ex = assertThrows(DomainException.class, () -> rejectFollowRequestUseCase.execute(100L, "req-99"));
        assertThat(ex.getError()).isEqualTo(ProfileError.FOLLOW_REQUEST_NOT_FOUND);
    }
}
