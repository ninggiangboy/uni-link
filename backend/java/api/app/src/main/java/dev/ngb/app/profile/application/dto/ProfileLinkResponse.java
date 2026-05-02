package dev.ngb.app.profile.application.dto;

import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.model.profile.ProfileLinkType;

public record ProfileLinkResponse(
        String linkUuid,
        ProfileLinkType type,
        String url,
        Integer orderIndex
) {
    public static ProfileLinkResponse of(ProfileLink link) {
        return new ProfileLinkResponse(link.getUuid(), link.getType(), link.getUrl(), link.getOrderIndex());
    }
}
