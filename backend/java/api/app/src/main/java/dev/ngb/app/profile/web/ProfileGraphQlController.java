package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.ProfileQueryService;
import dev.ngb.app.profile.application.dto.FollowRequestResponse;
import dev.ngb.app.profile.application.dto.PageQuery;
import dev.ngb.app.profile.application.dto.ProfileBrief;
import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.dto.ProfileMetadataResponse;
import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.dto.ProfileSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProfileGraphQlController {

    private final ProfileQueryService profileQueryService;

    @QueryMapping
    public ProfileSummary myProfile(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.getMyProfile(accountId);
    }

    @QueryMapping
    public ProfileSummary profile(@AuthenticationPrincipal Jwt jwt, @Argument String username) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.getProfileByUsername(username, Optional.of(accountId));
    }

    @QueryMapping
    public List<ProfileBrief> followers(
            @AuthenticationPrincipal Jwt jwt,
            @Argument String username,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.listFollowers(username, Optional.of(accountId), PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<ProfileBrief> following(
            @AuthenticationPrincipal Jwt jwt,
            @Argument String username,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.listFollowing(username, Optional.of(accountId), PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<FollowRequestResponse> pendingFollowRequests(
            @AuthenticationPrincipal Jwt jwt,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.listPendingFollowRequests(accountId, PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<ProfileLinkResponse> profileLinks(@Argument String username) {
        return profileQueryService.listProfileLinks(username);
    }

    @QueryMapping
    public List<ProfileMetadataResponse> myMetadata(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.listMetadata(accountId);
    }

    @QueryMapping
    public ProfileSettingResponse mySettings(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.getSettings(accountId);
    }

    @QueryMapping
    public List<ProfileBrief> blockedProfiles(
            @AuthenticationPrincipal Jwt jwt,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.listBlocked(accountId, PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<ProfileBrief> mutedProfiles(
            @AuthenticationPrincipal Jwt jwt,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = jwt.getClaim("account_id");
        return profileQueryService.listMuted(accountId, PageQuery.of(limit, offset));
    }
}
