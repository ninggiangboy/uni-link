package dev.ngb.app.profile.application.dto;

import dev.ngb.app.profile.ProfileConstants;

/**
 * Normalized page query parameters for relationship listings.
 * {@code limit} is clamped to [1, MAX_RELATIONSHIP_PAGE_SIZE]; {@code offset} is clamped to >= 0.
 */
public record PageQuery(int limit, int offset) {
    public PageQuery {
        if (limit <= 0) {
            limit = ProfileConstants.DEFAULT_RELATIONSHIP_PAGE_SIZE;
        }
        if (limit > ProfileConstants.MAX_RELATIONSHIP_PAGE_SIZE) {
            limit = ProfileConstants.MAX_RELATIONSHIP_PAGE_SIZE;
        }
        if (offset < 0) {
            offset = 0;
        }
    }

    public static PageQuery of(Integer limit, Integer offset) {
        return new PageQuery(
                limit == null ? ProfileConstants.DEFAULT_RELATIONSHIP_PAGE_SIZE : limit,
                offset == null ? 0 : offset
        );
    }
}
