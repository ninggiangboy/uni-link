package dev.ngb.infrastructure.neo4j.profile.query;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProfileRelationshipCypher {

    public static final String FOLLOW = """
            MERGE (src:Profile {profileId: $sourceId})
            MERGE (dst:Profile {profileId: $targetId})
            OPTIONAL MATCH (src)-[existing:FOLLOWS]->(dst)
            WITH src, dst, existing IS NULL AS created
            MERGE (src)-[r:FOLLOWS]->(dst)
            ON CREATE SET r.since = $since
            RETURN created
            """;

    public static final String UNFOLLOW = """
            OPTIONAL MATCH (src:Profile {profileId: $sourceId})-[r:FOLLOWS]->(dst:Profile {profileId: $targetId})
            WITH collect(r) AS rels
            FOREACH (rel IN rels | DELETE rel)
            RETURN size(rels) > 0 AS deleted
            """;

    public static final String IS_FOLLOWING = """
            MATCH (src:Profile {profileId: $sourceId})-[r:FOLLOWS]->(dst:Profile {profileId: $targetId})
            RETURN count(r) > 0 AS exists
            """;

    public static final String BLOCK = """
            MERGE (src:Profile {profileId: $sourceId})
            MERGE (dst:Profile {profileId: $targetId})
            OPTIONAL MATCH (src)-[existing:BLOCKS]->(dst)
            WITH src, dst, existing IS NULL AS created
            MERGE (src)-[r:BLOCKS]->(dst)
            ON CREATE SET r.since = $since
            WITH src, dst, created
            OPTIONAL MATCH (src)-[f1:FOLLOWS]->(dst)
            DELETE f1
            WITH src, dst, created
            OPTIONAL MATCH (dst)-[f2:FOLLOWS]->(src)
            DELETE f2
            RETURN created
            """;

    public static final String UNBLOCK = """
            OPTIONAL MATCH (src:Profile {profileId: $sourceId})-[r:BLOCKS]->(dst:Profile {profileId: $targetId})
            WITH collect(r) AS rels
            FOREACH (rel IN rels | DELETE rel)
            RETURN size(rels) > 0 AS deleted
            """;

    public static final String IS_BLOCKED = """
            MATCH (src:Profile {profileId: $sourceId})-[r:BLOCKS]->(dst:Profile {profileId: $targetId})
            RETURN count(r) > 0 AS exists
            """;

    public static final String MUTE = """
            MERGE (src:Profile {profileId: $sourceId})
            MERGE (dst:Profile {profileId: $targetId})
            OPTIONAL MATCH (src)-[existing:MUTES]->(dst)
            WITH src, dst, existing IS NULL AS created
            MERGE (src)-[r:MUTES]->(dst)
            ON CREATE SET r.since = $since
            RETURN created
            """;

    public static final String UNMUTE = """
            OPTIONAL MATCH (src:Profile {profileId: $sourceId})-[r:MUTES]->(dst:Profile {profileId: $targetId})
            WITH collect(r) AS rels
            FOREACH (rel IN rels | DELETE rel)
            RETURN size(rels) > 0 AS deleted
            """;

    public static final String IS_MUTED = """
            MATCH (src:Profile {profileId: $sourceId})-[r:MUTES]->(dst:Profile {profileId: $targetId})
            RETURN count(r) > 0 AS exists
            """;

    public static final String FOLLOW_HASHTAG = """
            MERGE (p:Profile {profileId: $profileId})
            MERGE (h:Hashtag {hashtagId: $hashtagId})
            OPTIONAL MATCH (p)-[existing:FOLLOWS_TAG]->(h)
            WITH p, h, existing IS NULL AS created
            MERGE (p)-[r:FOLLOWS_TAG]->(h)
            ON CREATE SET r.since = $since
            RETURN created
            """;

    public static final String UNFOLLOW_HASHTAG = """
            OPTIONAL MATCH (p:Profile {profileId: $profileId})-[r:FOLLOWS_TAG]->(h:Hashtag {hashtagId: $hashtagId})
            WITH collect(r) AS rels
            FOREACH (rel IN rels | DELETE rel)
            RETURN size(rels) > 0 AS deleted
            """;

    public static final String FIND_FOLLOWER_PROFILE_IDS = """
            MATCH (p:Profile {profileId: $profileId})<-[r:FOLLOWS]-(f:Profile)
            WHERE NOT (p)-[:BLOCKS]->(f) AND NOT (f)-[:BLOCKS]->(p)
            RETURN f.profileId AS profileId
            %s
            SKIP $offset
            LIMIT $limit
            """;

    public static final String FIND_FOLLOWING_PROFILE_IDS = """
            MATCH (p:Profile {profileId: $profileId})-[r:FOLLOWS]->(f:Profile)
            WHERE NOT (p)-[:BLOCKS]->(f) AND NOT (f)-[:BLOCKS]->(p)
            RETURN f.profileId AS profileId
            %s
            SKIP $offset
            LIMIT $limit
            """;

    public static final String FIND_MUTUAL_FOLLOWING_PROFILE_IDS = """
            MATCH (:Profile {profileId: $profileId})-[r1:FOLLOWS]->(mutual:Profile)<-[r2:FOLLOWS]-(:Profile {profileId: $otherProfileId})
            WHERE mutual.profileId <> $profileId AND mutual.profileId <> $otherProfileId
            WITH mutual, max(r1.since, r2.since) AS since
            RETURN mutual.profileId AS profileId
            %s
            LIMIT $limit
            """;

    public static final String FIND_FOLLOWED_HASHTAG_IDS = """
            MATCH (p:Profile {profileId: $profileId})-[r:FOLLOWS_TAG]->(h:Hashtag)
            RETURN h.hashtagId AS hashtagId
            %s
            SKIP $offset
            LIMIT $limit
            """;
}
