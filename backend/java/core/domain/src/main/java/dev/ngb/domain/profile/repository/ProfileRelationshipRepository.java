package dev.ngb.domain.profile.repository;

import dev.ngb.domain.profile.model.relationship.ProfileRelationshipState;

import java.util.List;

/**
 * Graph-oriented repository for profile relationship operations.
 * <p>
 * This contract intentionally does not extend the generic CRUD repository
 * because relationship queries are traversal-oriented and Neo4j-specific.
 */
public interface ProfileRelationshipRepository {

    boolean follow(Long followerProfileId, Long followingProfileId);

    boolean unfollow(Long followerProfileId, Long followingProfileId);

    boolean isFollowing(Long followerProfileId, Long followingProfileId);

    boolean blockAndCleanupFollows(Long blockerProfileId, Long blockedProfileId);

    boolean unblock(Long blockerProfileId, Long blockedProfileId);

    boolean isBlocked(Long blockerProfileId, Long blockedProfileId);

    boolean mute(Long muterProfileId, Long mutedProfileId);

    boolean unmute(Long muterProfileId, Long mutedProfileId);

    boolean isMuted(Long muterProfileId, Long mutedProfileId);

    ProfileRelationshipState findRelationshipsBetween(Long profileIdA, Long profileIdB);

    boolean followHashtag(Long profileId, Long hashtagId);

    boolean unfollowHashtag(Long profileId, Long hashtagId);

    List<Long> findFollowerProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findFollowingProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findBlockedProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findMutedProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findMutualFollowingProfileIds(Long profileId, Long otherProfileId, int limit, ProfileRelationshipSort sort);

    List<Long> findFollowedHashtagIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);
}
