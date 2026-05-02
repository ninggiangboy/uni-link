package dev.ngb.domain.profile.model.profile;

import dev.ngb.domain.DomainEntity;
import dev.ngb.util.NullUtils;
import dev.ngb.util.StringUtils;
import lombok.Getter;

import java.time.Instant;

/**
 * Public persona of a user, separated from the authentication account.
 * <p>
 * Aggregate root of the profile domain. Activity, links, media, metadata, stats,
 * settings, and username history are separate entities with their own repositories.
 */
@Getter
public class Profile extends DomainEntity<Long> {

    private Profile() {}

    private Long accountId;

    private String username;
    private String displayName;

    private String bio;
    private String website;
    private String location;

    private String avatarUrl;
    private String bannerUrl;

    private ProfileVisibility visibility;
    private Boolean isVerified;
    private String publicKey;

    public static Profile reconstruct(
            Long id, String uuid, Long createdBy, Instant createdAt, Long updatedBy, Instant updatedAt,
            Long accountId, String username, String displayName, String bio, String website, String location,
            String avatarUrl, String bannerUrl, ProfileVisibility visibility, Boolean isVerified, String publicKey) {
        Profile obj = new Profile();
        obj.id = id;
        obj.uuid = uuid;
        obj.createdBy = createdBy;
        obj.createdAt = createdAt;
        obj.updatedBy = updatedBy;
        obj.updatedAt = updatedAt;
        obj.accountId = accountId;
        obj.username = username;
        obj.displayName = displayName;
        obj.bio = bio;
        obj.website = website;
        obj.location = location;
        obj.avatarUrl = avatarUrl;
        obj.bannerUrl = bannerUrl;
        obj.visibility = visibility;
        obj.isVerified = isVerified;
        obj.publicKey = publicKey;
        return obj;
    }

    public static Profile createForNewAccount(
            Long accountId,
            String username,
            String displayName,
            String bio,
            ProfileVisibility visibility
    ) {
        Profile obj = new Profile();
        Instant now = Instant.now(obj.clock);
        obj.createdAt = now;
        obj.updatedAt = now;
        obj.accountId = accountId;
        obj.username = StringUtils.trim(username);
        obj.displayName = StringUtils.trim(displayName);
        obj.bio = StringUtils.trim(bio);
        obj.visibility = NullUtils.getOr(visibility, ProfileVisibility.PUBLIC);
        obj.isVerified = Boolean.FALSE;
        return obj;
    }

    /**
     * Updates the editable persona fields. {@code null} values clear optional fields;
     * {@code displayName} must not be blank.
     */
    public void updateInfo(String displayName, String bio, String website, String location) {
        this.displayName = StringUtils.trim(displayName);
        this.bio = StringUtils.trim(bio);
        this.website = StringUtils.trim(website);
        this.location = StringUtils.trim(location);
        touch();
    }

    public void changeVisibility(ProfileVisibility visibility) {
        this.visibility = NullUtils.getOr(visibility, ProfileVisibility.PUBLIC);
        touch();
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        touch();
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
        touch();
    }

    public void changeUsername(String username) {
        this.username = StringUtils.trim(username);
        touch();
    }

    public void registerPublicKey(String publicKey) {
        this.publicKey = publicKey;
        touch();
    }

    public boolean isHidden() {
        return visibility == ProfileVisibility.HIDDEN;
    }

    public boolean isPrivate() {
        return visibility == ProfileVisibility.PRIVATE;
    }

    private void touch() {
        this.updatedAt = Instant.now(clock);
    }
}
