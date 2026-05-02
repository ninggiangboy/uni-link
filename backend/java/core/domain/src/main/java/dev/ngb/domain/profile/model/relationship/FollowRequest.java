package dev.ngb.domain.profile.model.relationship;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * A pending follow request from {@code requesterProfileId} to {@code targetProfileId}.
 * <p>
 * Created when a user attempts to follow a {@code PRIVATE} profile. Resolves to
 * {@link FollowRequestStatus#APPROVED} (creates the FOLLOWS edge), {@link FollowRequestStatus#REJECTED},
 * or {@link FollowRequestStatus#CANCELLED} (auto on block / requester unfollow attempt).
 */
@Getter
public class FollowRequest extends DomainEntity<Long> {

    private FollowRequest() {}

    private Long requesterProfileId;
    private Long targetProfileId;
    private FollowRequestStatus status;
    private Instant respondedAt;

    public static FollowRequest reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long requesterProfileId, Long targetProfileId, FollowRequestStatus status, Instant respondedAt) {
        FollowRequest obj = new FollowRequest();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.requesterProfileId = requesterProfileId;
        obj.targetProfileId = targetProfileId;
        obj.status = status;
        obj.respondedAt = respondedAt;
        return obj;
    }

    public static FollowRequest createPending(Long requesterProfileId, Long targetProfileId) {
        FollowRequest obj = new FollowRequest();
        Instant now = Instant.now(obj.clock);
        obj.createdAt = now;
        obj.updatedAt = now;
        obj.requesterProfileId = requesterProfileId;
        obj.targetProfileId = targetProfileId;
        obj.status = FollowRequestStatus.PENDING;
        return obj;
    }

    public boolean isPending() {
        return status == FollowRequestStatus.PENDING;
    }

    public void approve() {
        transitionTo(FollowRequestStatus.APPROVED);
    }

    public void reject() {
        transitionTo(FollowRequestStatus.REJECTED);
    }

    public void cancel() {
        transitionTo(FollowRequestStatus.CANCELLED);
    }

    private void transitionTo(FollowRequestStatus next) {
        Instant now = Instant.now(clock);
        this.status = next;
        this.respondedAt = now;
        this.updatedAt = now;
    }
}
