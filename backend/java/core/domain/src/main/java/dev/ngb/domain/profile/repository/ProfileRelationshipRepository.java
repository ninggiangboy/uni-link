package dev.ngb.domain.profile.repository;

import java.time.Instant;
import java.util.List;

/**
 * Graph-oriented repository for profile relationship operations.
 * <p>
 * This contract intentionally does not extend the generic CRUD repository
 * because relationship queries are traversal-oriented and Neo4j-specific.
 */
public interface ProfileRelationshipRepository {

    boolean follow(Long followerProfileId, Long followingProfileId, Instant since);

    boolean unfollow(Long followerProfileId, Long followingProfileId);

    boolean isFollowing(Long followerProfileId, Long followingProfileId);

    boolean block(Long blockerProfileId, Long blockedProfileId, Instant since);

    boolean unblock(Long blockerProfileId, Long blockedProfileId);

    boolean isBlocked(Long blockerProfileId, Long blockedProfileId);

    boolean mute(Long muterProfileId, Long mutedProfileId, Instant since);

    boolean unmute(Long muterProfileId, Long mutedProfileId);

    boolean isMuted(Long muterProfileId, Long mutedProfileId);

    boolean followHashtag(Long profileId, Long hashtagId, Instant since);

    boolean unfollowHashtag(Long profileId, Long hashtagId);

    List<Long> findFollowerProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findFollowingProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findBlockedProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findMutedProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);

    List<Long> findMutualFollowingProfileIds(Long profileId, Long otherProfileId, int limit, ProfileRelationshipSort sort);

    List<Long> findFollowedHashtagIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort);
}
