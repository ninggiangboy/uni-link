package dev.ngb.domain.thread.model.thread;

/**
 * Controls who is allowed to reply to a thread.
 */
public enum ReplyRestriction {
    EVERYONE,
    FOLLOWERS_ONLY,
    MENTIONED_ONLY,
    NOBODY
}
