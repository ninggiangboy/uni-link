package dev.ngb.app.profile.application.query;

import dev.ngb.app.profile.application.query.dto.FollowRequestResponse;
import dev.ngb.app.profile.application.query.dto.PageQuery;
import dev.ngb.app.profile.application.query.dto.ProfileBrief;
import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.dto.ProfileMetadataResponse;
import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.dto.ProfileSummary;
import dev.ngb.application.UseCaseService;
import dev.ngb.application.port.follow.FollowDeltaIncrementPort;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.model.setting.ProfileSetting;
import dev.ngb.domain.profile.model.stats.ProfileStats;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipSort;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileSettingRepository;
import dev.ngb.domain.profile.repository.ProfileStatsRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * Consolidated read-side operations for profiles (formerly separate query use cases).
 */
@RequiredArgsConstructor
public class ProfileQueryService implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileStatsRepository profileStatsRepository;
    private final ProfileRelationshipRepository profileRelationshipRepository;
    private final ProfileLinkRepository profileLinkRepository;
    private final ProfileMetadataRepository profileMetadataRepository;
    private final ProfileSettingRepository profileSettingRepository;
    private final FollowRequestRepository followRequestRepository;
    private final FollowDeltaIncrementPort followDeltaIncrementPort;

    public ProfileSummary getMyProfile(Long accountId) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        ProfileStats stats = profileStatsRepository.findByProfileId(profile.getId()).orElse(null);
        long followerDelta = followDeltaIncrementPort.getFollowerDelta(profile.getId());
        long followingDelta = followDeltaIncrementPort.getFollowingDelta(profile.getId());
        return ProfileSummary.of(profile, stats, followerDelta, followingDelta);
    }

    /**
     * Visibility-aware profile lookup for any username.
     */
    public ProfileSummary getProfileByUsername(String username, Optional<Long> viewerAccountId) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        Optional<Profile> viewerProfile = viewerAccountId.flatMap(profileRepository::findByAccountId);
        boolean isOwner = viewerProfile.map(v -> v.getId().equals(profile.getId())).orElse(false);

        if (!isOwner) {
            if (profile.isHidden()) {
                throw ProfileError.PROFILE_NOT_FOUND.exception();
            }
            if (viewerProfile.isPresent()
                    && profileRelationshipRepository.isBlocked(profile.getId(), viewerProfile.get().getId())) {
                throw ProfileError.PROFILE_NOT_FOUND.exception();
            }
        }

        ProfileStats stats = profileStatsRepository.findByProfileId(profile.getId()).orElse(null);
        long followerDelta = followDeltaIncrementPort.getFollowerDelta(profile.getId());
        long followingDelta = followDeltaIncrementPort.getFollowingDelta(profile.getId());
        ProfileSummary summary = ProfileSummary.of(profile, stats, followerDelta, followingDelta);

        if (!isOwner && profile.isPrivate()) {
            boolean isFollower = viewerProfile
                    .map(v -> profileRelationshipRepository.isFollowing(v.getId(), profile.getId()))
                    .orElse(false);
            if (!isFollower) {
                return summary.withRestrictedFields();
            }
        }
        return summary;
    }

    /**
     * Paginated follower list with visibility rules for non-owners.
     */
    public List<ProfileBrief> listFollowers(String username, Optional<Long> viewerAccountId, PageQuery page) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        Optional<Profile> viewer = viewerAccountId.flatMap(profileRepository::findByAccountId);
        boolean isOwner = viewer.map(v -> v.getId().equals(profile.getId())).orElse(false);

        if (!isOwner) {
            if (profile.isHidden()) throw ProfileError.PROFILE_NOT_FOUND.exception();
            if (profile.isPrivate()) {
                boolean isFollower = viewer
                        .map(v -> profileRelationshipRepository.isFollowing(v.getId(), profile.getId()))
                        .orElse(false);
                if (!isFollower) throw ProfileError.PROFILE_NOT_FOUND.exception();
            }
        }

        List<Long> followerIds = profileRelationshipRepository.findFollowerProfileIds(
                profile.getId(), page.limit(), page.offset(), ProfileRelationshipSort.LATEST);
        if (followerIds.isEmpty()) return List.of();

        Map<Long, Profile> byId = new HashMap<>();
        for (Profile p : profileRepository.findByIds(followerIds)) {
            byId.put(p.getId(), p);
        }
        return followerIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(ProfileBrief::of)
                .toList();
    }

    public List<ProfileBrief> listFollowing(String username, Optional<Long> viewerAccountId, PageQuery page) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        Optional<Profile> viewer = viewerAccountId.flatMap(profileRepository::findByAccountId);
        boolean isOwner = viewer.map(v -> v.getId().equals(profile.getId())).orElse(false);

        if (!isOwner) {
            if (profile.isHidden()) throw ProfileError.PROFILE_NOT_FOUND.exception();
            if (profile.isPrivate()) {
                boolean isFollower = viewer
                        .map(v -> profileRelationshipRepository.isFollowing(v.getId(), profile.getId()))
                        .orElse(false);
                if (!isFollower) throw ProfileError.PROFILE_NOT_FOUND.exception();
            }
        }

        List<Long> followingIds = profileRelationshipRepository.findFollowingProfileIds(
                profile.getId(), page.limit(), page.offset(), ProfileRelationshipSort.LATEST);
        if (followingIds.isEmpty()) return List.of();

        Map<Long, Profile> byId = new HashMap<>();
        for (Profile p : profileRepository.findByIds(followingIds)) {
            byId.put(p.getId(), p);
        }
        return followingIds.stream()
                .map(byId::get)
                .filter(Objects::nonNull)
                .map(ProfileBrief::of)
                .toList();
    }

    public List<FollowRequestResponse> listPendingFollowRequests(Long accountId, PageQuery page) {
        Profile owner = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        List<FollowRequest> pending = followRequestRepository.findPendingByTargetProfileId(
                owner.getId(), page.limit(), page.offset());
        if (pending.isEmpty()) return List.of();

        List<Long> requesterIds = pending.stream().map(FollowRequest::getRequesterProfileId).distinct().toList();
        Map<Long, Profile> byId = new HashMap<>();
        for (Profile p : profileRepository.findByIds(requesterIds)) {
            byId.put(p.getId(), p);
        }
        return pending.stream()
                .map(req -> FollowRequestResponse.of(req, byId.get(req.getRequesterProfileId())))
                .toList();
    }

    public List<ProfileLinkResponse> listProfileLinks(String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        if (profile.isHidden()) {
            throw ProfileError.PROFILE_NOT_FOUND.exception();
        }
        return profileLinkRepository.findByProfileId(profile.getId()).stream()
                .sorted(Comparator
                        .comparingInt((ProfileLink l) ->
                                l.getOrderIndex() == null ? Integer.MAX_VALUE : l.getOrderIndex())
                        .thenComparing(ProfileLink::getCreatedAt))
                .map(ProfileLinkResponse::of)
                .toList();
    }

    public List<ProfileMetadataResponse> listMetadata(Long accountId) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        return profileMetadataRepository.findByProfileId(profile.getId()).stream()
                .map(ProfileMetadataResponse::of)
                .toList();
    }

    public ProfileSettingResponse getSettings(Long accountId) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        ProfileSetting setting = profileSettingRepository.findByProfileId(profile.getId())
                .orElseGet(() -> profileSettingRepository.save(ProfileSetting.createDefault(profile.getId())));
        return ProfileSettingResponse.of(setting);
    }

    public List<ProfileBrief> listBlocked(Long accountId, PageQuery page) {
        Profile owner = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        List<Long> ids = profileRelationshipRepository.findBlockedProfileIds(
                owner.getId(), page.limit(), page.offset(), ProfileRelationshipSort.LATEST);
        if (ids.isEmpty()) return List.of();

        Map<Long, Profile> byId = new HashMap<>();
        for (Profile p : profileRepository.findByIds(ids)) {
            byId.put(p.getId(), p);
        }
        return ids.stream().map(byId::get).filter(Objects::nonNull).map(ProfileBrief::of).toList();
    }

    public List<ProfileBrief> listMuted(Long accountId, PageQuery page) {
        Profile owner = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        List<Long> ids = profileRelationshipRepository.findMutedProfileIds(
                owner.getId(), page.limit(), page.offset(), ProfileRelationshipSort.LATEST);
        if (ids.isEmpty()) return List.of();

        Map<Long, Profile> byId = new HashMap<>();
        for (Profile p : profileRepository.findByIds(ids)) {
            byId.put(p.getId(), p);
        }
        return ids.stream().map(byId::get).filter(p -> p != null).map(ProfileBrief::of).toList();
    }
}
