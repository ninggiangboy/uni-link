package dev.ngb.app.profile.application.usecase.social.follow_profile.dto;

/**
 * Response from a follow attempt.
 *
 * @param status either {@code FOLLOWING} (edge created) or {@code REQUESTED} (pending FollowRequest created)
 * @param followRequestUuid populated only when {@code status == REQUESTED}
 */
public record FollowResponse(Status status, String followRequestUuid) {
    public enum Status { FOLLOWING, REQUESTED }

    public static FollowResponse following() {
        return new FollowResponse(Status.FOLLOWING, null);
    }

    public static FollowResponse requested(String followRequestUuid) {
        return new FollowResponse(Status.REQUESTED, followRequestUuid);
    }
}
