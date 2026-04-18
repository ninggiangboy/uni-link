package dev.ngb.domain.group.model.group;

/**
 * Visibility levels for a group.
 * <ul>
 *   <li>{@code PUBLIC} – discoverable by anyone; anyone can join directly.</li>
 *   <li>{@code PRIVATE} – discoverable; joining requires a request or invitation.</li>
 *   <li>{@code SECRET} – not discoverable; only reachable via invitation.</li>
 * </ul>
 */
public enum GroupVisibility {
    PUBLIC,
    PRIVATE,
    SECRET
}
