package dev.ngb.constant;

public final class TopicNames {
    public static final String OUTBOX_EVENTS = "outbox-events";

    public static final String JOB_TRIGGERED = "job.triggered";

    /** Follow / unfollow denormalized counter deltas (see {@code ProfileFollowStatsDeltaEvent} in libs-common). */
    public static final String PROFILE_FOLLOW_STATS_DELTA = "profile.follow-stats-delta";

    public static final String REALTIME_PUSH_CHANNEL = "realtime:push";
}
