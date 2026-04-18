package dev.ngb.domain.notification.model.profile;

import dev.ngb.domain.DomainEntity;
import lombok.Getter;

import java.time.Instant;

/**
 * Per-profile notification preferences controlling which types and channels are enabled.
 */
@Getter
public class NotificationSetting extends DomainEntity<Long> {

    private NotificationSetting() {}

    private Long profileId;

    private Boolean likeEnabled;
    private Boolean replyEnabled;
    private Boolean mentionEnabled;
    private Boolean followEnabled;
    private Boolean repostEnabled;
    private Boolean quoteEnabled;

    private Boolean pushEnabled;
    private Boolean emailEnabled;

    public static NotificationSetting reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long profileId, Boolean likeEnabled, Boolean replyEnabled, Boolean mentionEnabled,
            Boolean followEnabled, Boolean repostEnabled, Boolean quoteEnabled,
            Boolean pushEnabled, Boolean emailEnabled) {
        NotificationSetting obj = new NotificationSetting();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.profileId = profileId;
        obj.likeEnabled = likeEnabled;
        obj.replyEnabled = replyEnabled;
        obj.mentionEnabled = mentionEnabled;
        obj.followEnabled = followEnabled;
        obj.repostEnabled = repostEnabled;
        obj.quoteEnabled = quoteEnabled;
        obj.pushEnabled = pushEnabled;
        obj.emailEnabled = emailEnabled;
        return obj;
    }
}
