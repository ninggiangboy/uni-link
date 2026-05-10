package dev.ngb.app.profile.usecase;

import dev.ngb.app.profile.application.query.ProfileQueryService;
import dev.ngb.app.profile.application.dto.PageQuery;
import dev.ngb.app.profile.support.ProfileFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.ProfileVisibility;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipSort;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileQueryService")
class ProfileQueryServiceTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileStatsRepository profileStatsRepository;
    @Mock private ProfileRelationshipRepository profileRelationshipRepository;
    @Mock private ProfileLinkRepository profileLinkRepository;
    @Mock private ProfileMetadataRepository profileMetadataRepository;
    @Mock private ProfileSettingRepository profileSettingRepository;
    @Mock private FollowRequestRepository followRequestRepository;

    @InjectMocks private ProfileQueryService profileQueryService;

    @Test
    @DisplayName("getMyProfile: owns profile -> returns summary with stats")
    void getMyProfileWhenOwnsProfileReturnsSummary() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        var stats = ProfileFixtures.stats(10L, 1L, 7L, 3L);
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(profile));
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.of(stats));

        var summary = profileQueryService.getMyProfile(100L);

        assertThat(summary.username()).isEqualTo("alice");
        assertThat(summary.followerCount()).isEqualTo(7L);
        assertThat(summary.followingCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("getMyProfile: stats row missing -> zeros are returned")
    void getMyProfileWhenStatsMissingReturnsZeros() {
        var profile = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByAccountId(200L)).thenReturn(Optional.of(profile));
        when(profileStatsRepository.findByProfileId(2L)).thenReturn(Optional.empty());

        var summary = profileQueryService.getMyProfile(200L);

        assertThat(summary.followerCount()).isZero();
        assertThat(summary.followingCount()).isZero();
    }

    @Test
    @DisplayName("getMyProfile: no profile -> PROFILE_NOT_FOUND")
    void getMyProfileWhenNoProfileThrowsNotFound() {
        when(profileRepository.findByAccountId(300L)).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> profileQueryService.getMyProfile(300L));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("getProfileByUsername: public profile, anonymous viewer -> full summary")
    void getProfileByUsernameWhenPublicAnonReturnsFull() {
        var target = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(target));
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.of(ProfileFixtures.stats(10L, 1L, 5, 2)));

        var summary = profileQueryService.getProfileByUsername("alice", Optional.empty());

        assertThat(summary.bio()).isNotNull();
        assertThat(summary.followerCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("getProfileByUsername: hidden profile, non-owner -> 404")
    void getProfileByUsernameWhenHiddenNonOwnerThrowsNotFound() {
        var target = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.HIDDEN);
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(target));

        var ex = assertThrows(DomainException.class, () -> profileQueryService.getProfileByUsername("alice", Optional.empty()));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("getProfileByUsername: hidden profile, owner -> full summary")
    void getProfileByUsernameWhenHiddenOwnerReturnsFull() {
        var target = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.HIDDEN);
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(target));
        when(profileRepository.findByAccountId(100L)).thenReturn(Optional.of(target));
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.empty());

        var summary = profileQueryService.getProfileByUsername("alice", Optional.of(100L));
        assertThat(summary.username()).isEqualTo("alice");
    }

    @Test
    @DisplayName("getProfileByUsername: blocked viewer -> 404")
    void getProfileByUsernameWhenBlockedReturnsNotFound() {
        var target = ProfileFixtures.profile(1L, 100L, "alice");
        var viewer = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(target));
        when(profileRepository.findByAccountId(200L)).thenReturn(Optional.of(viewer));
        when(profileRelationshipRepository.isBlocked(1L, 2L)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> profileQueryService.getProfileByUsername("alice", Optional.of(200L)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("getProfileByUsername: private profile, non-follower -> stripped fields")
    void getProfileByUsernameWhenPrivateNonFollowerReturnsStripped() {
        var target = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.PRIVATE);
        var viewer = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(target));
        when(profileRepository.findByAccountId(200L)).thenReturn(Optional.of(viewer));
        when(profileRelationshipRepository.isBlocked(1L, 2L)).thenReturn(false);
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.of(ProfileFixtures.stats(10L, 1L, 9, 3)));
        when(profileRelationshipRepository.isFollowing(2L, 1L)).thenReturn(false);

        var summary = profileQueryService.getProfileByUsername("alice", Optional.of(200L));

        assertThat(summary.bio()).isNull();
        assertThat(summary.threadCount()).isZero();
    }

    @Test
    @DisplayName("getProfileByUsername: private profile, approved follower -> full summary")
    void getProfileByUsernameWhenPrivateFollowerReturnsFull() {
        var target = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.PRIVATE);
        var viewer = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(target));
        when(profileRepository.findByAccountId(200L)).thenReturn(Optional.of(viewer));
        when(profileRelationshipRepository.isBlocked(1L, 2L)).thenReturn(false);
        when(profileStatsRepository.findByProfileId(1L)).thenReturn(Optional.empty());
        when(profileRelationshipRepository.isFollowing(2L, 1L)).thenReturn(true);

        var summary = profileQueryService.getProfileByUsername("alice", Optional.of(200L));
        assertThat(summary.bio()).isNotNull();
    }

    @Test
    @DisplayName("getProfileByUsername: unknown username -> PROFILE_NOT_FOUND")
    void getProfileByUsernameWhenMissingThrowsNotFound() {
        when(profileRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        var ex = assertThrows(DomainException.class, () -> profileQueryService.getProfileByUsername("ghost", Optional.empty()));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("listFollowers: public profile -> followers returned in graph order")
    void listFollowersWhenPublic() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));
        when(profileRelationshipRepository.findFollowerProfileIds(eq(1L), anyInt(), anyInt(), eq(ProfileRelationshipSort.LATEST)))
                .thenReturn(List.of(2L, 3L));
        when(profileRepository.findByIds(List.of(2L, 3L))).thenReturn(List.of(
                ProfileFixtures.profile(2L, 200L, "bob"),
                ProfileFixtures.profile(3L, 300L, "carol")
        ));

        var followers = profileQueryService.listFollowers("alice", Optional.empty(), PageQuery.of(20, 0));
        assertThat(followers).extracting("username").containsExactly("bob", "carol");
    }

    @Test
    @DisplayName("listFollowers: hidden profile, non-owner -> 404")
    void listFollowersWhenHidden() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.HIDDEN);
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));

        var ex = assertThrows(DomainException.class,
                () -> profileQueryService.listFollowers("alice", Optional.empty(), PageQuery.of(20, 0)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("listFollowers: private profile, non-follower -> 404")
    void listFollowersWhenPrivateNonFollower() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.PRIVATE);
        var viewer = ProfileFixtures.profile(2L, 200L, "bob");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));
        when(profileRepository.findByAccountId(200L)).thenReturn(Optional.of(viewer));
        when(profileRelationshipRepository.isFollowing(2L, 1L)).thenReturn(false);

        var ex = assertThrows(DomainException.class,
                () -> profileQueryService.listFollowers("alice", Optional.of(200L), PageQuery.of(20, 0)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("listFollowers: empty graph result -> empty list")
    void listFollowersWhenEmpty() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));
        when(profileRelationshipRepository.findFollowerProfileIds(eq(1L), anyInt(), anyInt(), any(ProfileRelationshipSort.class)))
                .thenReturn(List.of());

        var followers = profileQueryService.listFollowers("alice", Optional.empty(), PageQuery.of(20, 0));
        assertThat(followers).isEmpty();
    }

    @Test
    @DisplayName("listFollowing: public profile -> returns following list")
    void listFollowingWhenPublic() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice");
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));
        when(profileRelationshipRepository.findFollowingProfileIds(eq(1L), anyInt(), anyInt(), eq(ProfileRelationshipSort.LATEST)))
                .thenReturn(List.of(2L));
        when(profileRepository.findByIds(List.of(2L))).thenReturn(List.of(ProfileFixtures.profile(2L, 200L, "bob")));

        var resp = profileQueryService.listFollowing("alice", Optional.empty(), PageQuery.of(20, 0));
        assertThat(resp).extracting("username").containsExactly("bob");
    }

    @Test
    @DisplayName("listFollowing: hidden profile non-owner -> 404")
    void listFollowingWhenHidden() {
        var profile = ProfileFixtures.profile(1L, 100L, "alice", ProfileVisibility.HIDDEN);
        when(profileRepository.findByUsername("alice")).thenReturn(Optional.of(profile));
        var ex = assertThrows(DomainException.class,
                () -> profileQueryService.listFollowing("alice", Optional.empty(), PageQuery.of(20, 0)));
        assertThat(ex.getError()).isEqualTo(ProfileError.PROFILE_NOT_FOUND);
    }
}
