package dev.ngb.app.profile.application.dto;

import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.relationship.FollowRequest;

import java.time.Instant;

public record FollowRequestResponse(
        String followRequestUuid,
        ProfileBrief requester,
        Instant createdAt
) {
    public static FollowRequestResponse of(FollowRequest request, Profile requester) {
        return new FollowRequestResponse(
                request.getUuid(),
                requester == null ? null : ProfileBrief.of(requester),
                request.getCreatedAt()
        );
    }
}
