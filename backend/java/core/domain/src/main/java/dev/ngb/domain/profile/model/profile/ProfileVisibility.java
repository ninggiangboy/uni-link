package dev.ngb.domain.profile.model.profile;

/**
 * Visibility levels for a profile.
 * <ul>
 *   <li>{@link #PUBLIC} — discoverable by anyone, including unauthenticated visitors.</li>
 *   <li>{@link #PRIVATE} — only approved followers may see threads, follower list, and bio.</li>
 *   <li>{@link #HIDDEN} — excluded from search and recommendations.</li>
 * </ul>
 */
public enum ProfileVisibility {
    PUBLIC,
    PRIVATE,
    HIDDEN
}
