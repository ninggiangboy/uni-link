package dev.ngb.infrastructure.neo4j.profile.repository;

import dev.ngb.domain.profile.model.relationship.ProfileRelationshipState;
import dev.ngb.domain.profile.repository.ProfileRelationshipRepository;
import dev.ngb.domain.profile.repository.ProfileRelationshipSort;
import dev.ngb.infrastructure.neo4j.profile.query.ProfileRelationshipCypher;
import dev.ngb.infrastructure.neo4j.support.Neo4jQueryExecutor;
import dev.ngb.util.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProfileRelationshipNeo4jRepository implements ProfileRelationshipRepository {

    private final Neo4jQueryExecutor queryExecutor;

    @Override
    public boolean follow(Long followerProfileId, Long followingProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.FOLLOW, Map.of(
                "sourceId", followerProfileId,
                "targetId", followingProfileId,
                "since", TimeProvider.now()
        ));
    }

    @Override
    public boolean unfollow(Long followerProfileId, Long followingProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.UNFOLLOW, Map.of(
                "sourceId", followerProfileId,
                "targetId", followingProfileId
        ));
    }

    @Override
    public boolean isFollowing(Long followerProfileId, Long followingProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.IS_FOLLOWING, Map.of(
                "sourceId", followerProfileId,
                "targetId", followingProfileId
        ));
    }

    @Override
    public boolean blockAndCleanupFollows(Long blockerProfileId, Long blockedProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.BLOCK_AND_CLEANUP_FOLLOWS, Map.of(
                "sourceId", blockerProfileId,
                "targetId", blockedProfileId,
                "since", TimeProvider.now()
        ));
    }

    @Override
    public boolean unblock(Long blockerProfileId, Long blockedProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.UNBLOCK, Map.of(
                "sourceId", blockerProfileId,
                "targetId", blockedProfileId
        ));
    }

    @Override
    public boolean isBlocked(Long blockerProfileId, Long blockedProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.IS_BLOCKED, Map.of(
                "sourceId", blockerProfileId,
                "targetId", blockedProfileId
        ));
    }

    @Override
    public boolean mute(Long muterProfileId, Long mutedProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.MUTE, Map.of(
                "sourceId", muterProfileId,
                "targetId", mutedProfileId,
                "since", TimeProvider.now()
        ));
    }

    @Override
    public boolean unmute(Long muterProfileId, Long mutedProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.UNMUTE, Map.of(
                "sourceId", muterProfileId,
                "targetId", mutedProfileId
        ));
    }

    @Override
    public boolean isMuted(Long muterProfileId, Long mutedProfileId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.IS_MUTED, Map.of(
                "sourceId", muterProfileId,
                "targetId", mutedProfileId
        ));
    }

    @Override
    public ProfileRelationshipState findRelationshipsBetween(Long profileIdA, Long profileIdB) {
        return queryExecutor.queryOne(ProfileRelationshipCypher.FIND_RELATIONSHIPS_BETWEEN, Map.of(
                "sourceId", profileIdA,
                "targetId", profileIdB
        ), (_, record) -> {
            List<String> sourceToTarget = record.get("sourceToTarget").asList(v -> v.asString());
            List<String> targetToSource = record.get("targetToSource").asList(v -> v.asString());
            return new ProfileRelationshipState(
                    sourceToTarget.contains("FOLLOWS"),
                    targetToSource.contains("FOLLOWS"),
                    sourceToTarget.contains("BLOCKS"),
                    targetToSource.contains("BLOCKS"),
                    sourceToTarget.contains("MUTES"),
                    targetToSource.contains("MUTES")
            );
        }, ProfileRelationshipState.class).orElse(ProfileRelationshipState.empty());
    }

    @Override
    public boolean followHashtag(Long profileId, Long hashtagId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.FOLLOW_HASHTAG, Map.of(
                "profileId", profileId,
                "hashtagId", hashtagId,
                "since", TimeProvider.now()
        ));
    }

    @Override
    public boolean unfollowHashtag(Long profileId, Long hashtagId) {
        return queryExecutor.queryBoolean(ProfileRelationshipCypher.UNFOLLOW_HASHTAG, Map.of(
                "profileId", profileId,
                "hashtagId", hashtagId
        ));
    }

    @Override
    public List<Long> findFollowerProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort) {
        String sortClause = switch (sort) {
            case LATEST -> "ORDER BY r.since DESC, f.profileId DESC";
            case OLDEST -> "ORDER BY r.since ASC, f.profileId ASC";
            case PROFILE_ID_ASC -> "ORDER BY f.profileId ASC";
            case PROFILE_ID_DESC -> "ORDER BY f.profileId DESC";
            case null -> "ORDER BY r.since DESC, f.profileId DESC";
        };

        return queryExecutor.queryLongList(ProfileRelationshipCypher.FIND_FOLLOWER_PROFILE_IDS.formatted(sortClause),
                Map.of(
                        "profileId", profileId,
                        "limit", limit,
                        "offset", offset
                ), "profileId");
    }

    @Override
    public List<Long> findFollowingProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort) {
        String sortClause = switch (sort) {
            case LATEST -> "ORDER BY r.since DESC, f.profileId DESC";
            case OLDEST -> "ORDER BY r.since ASC, f.profileId ASC";
            case PROFILE_ID_ASC -> "ORDER BY f.profileId ASC";
            case PROFILE_ID_DESC -> "ORDER BY f.profileId DESC";
            case null -> "ORDER BY r.since DESC, f.profileId DESC";
        };

        return queryExecutor.queryLongList(ProfileRelationshipCypher.FIND_FOLLOWING_PROFILE_IDS.formatted(sortClause), Map.of(
                "profileId", profileId,
                "limit", limit,
                "offset", offset
        ), "profileId");
    }

    @Override
    public List<Long> findBlockedProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort) {
        String sortClause = switch (sort) {
            case LATEST -> "ORDER BY r.since DESC, f.profileId DESC";
            case OLDEST -> "ORDER BY r.since ASC, f.profileId ASC";
            case PROFILE_ID_ASC -> "ORDER BY f.profileId ASC";
            case PROFILE_ID_DESC -> "ORDER BY f.profileId DESC";
            case null -> "ORDER BY r.since DESC, f.profileId DESC";
        };
        return queryExecutor.queryLongList(ProfileRelationshipCypher.FIND_BLOCKED_PROFILE_IDS.formatted(sortClause), Map.of(
                "profileId", profileId,
                "limit", limit,
                "offset", offset
        ), "profileId");
    }

    @Override
    public List<Long> findMutedProfileIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort) {
        String sortClause = switch (sort) {
            case LATEST -> "ORDER BY r.since DESC, f.profileId DESC";
            case OLDEST -> "ORDER BY r.since ASC, f.profileId ASC";
            case PROFILE_ID_ASC -> "ORDER BY f.profileId ASC";
            case PROFILE_ID_DESC -> "ORDER BY f.profileId DESC";
            case null -> "ORDER BY r.since DESC, f.profileId DESC";
        };
        return queryExecutor.queryLongList(ProfileRelationshipCypher.FIND_MUTED_PROFILE_IDS.formatted(sortClause), Map.of(
                "profileId", profileId,
                "limit", limit,
                "offset", offset
        ), "profileId");
    }

    @Override
    public List<Long> findMutualFollowingProfileIds(Long profileId, Long otherProfileId, int limit, ProfileRelationshipSort sort) {
        String sortClause = switch (sort) {
            case LATEST -> "ORDER BY since DESC, profileId DESC";
            case OLDEST -> "ORDER BY since ASC, profileId ASC";
            case PROFILE_ID_ASC -> "ORDER BY profileId ASC";
            case PROFILE_ID_DESC -> "ORDER BY profileId DESC";
            case null -> "ORDER BY since DESC, profileId DESC";
        };

        return queryExecutor.queryLongList(ProfileRelationshipCypher.FIND_MUTUAL_FOLLOWING_PROFILE_IDS.formatted(sortClause), Map.of(
                "profileId", profileId,
                "otherProfileId", otherProfileId,
                "limit", limit
        ), "profileId");
    }

    @Override
    public List<Long> findFollowedHashtagIds(Long profileId, int limit, int offset, ProfileRelationshipSort sort) {
        String sortClause = switch (sort) {
                case LATEST -> "ORDER BY r.since DESC, h.hashtagId DESC";
                case OLDEST -> "ORDER BY r.since ASC, h.hashtagId ASC";
                case PROFILE_ID_ASC -> "ORDER BY h.hashtagId ASC";
                case PROFILE_ID_DESC -> "ORDER BY h.hashtagId DESC";
                case null -> "ORDER BY r.since DESC, h.hashtagId DESC";
        };

        return queryExecutor.queryLongList(ProfileRelationshipCypher.FIND_FOLLOWED_HASHTAG_IDS.formatted(sortClause), Map.of(
                "profileId", profileId,
                "limit", limit,
                "offset", offset
        ), "hashtagId");
    }
}
