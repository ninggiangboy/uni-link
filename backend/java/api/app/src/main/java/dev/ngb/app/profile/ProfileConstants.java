package dev.ngb.app.profile;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProfileConstants {

    /** FR-02.1.4 — maximum number of external links per profile. */
    public static final int MAX_PROFILE_LINKS = 10;

    /** Maximum length of a metadata key. */
    public static final int MAX_METADATA_KEY_LENGTH = 64;

    /** Maximum length of a metadata value. */
    public static final int MAX_METADATA_VALUE_LENGTH = 1000;

    /** Default page size for relationship listings (followers, following, blocks, mutes, follow-requests). */
    public static final int DEFAULT_RELATIONSHIP_PAGE_SIZE = 20;

    /** Maximum page size for relationship listings. */
    public static final int MAX_RELATIONSHIP_PAGE_SIZE = 100;
}
