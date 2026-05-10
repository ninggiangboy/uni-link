package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.query.ProfileQueryService;
import dev.ngb.app.profile.application.dto.FollowRequestResponse;
import dev.ngb.app.profile.application.dto.PageQuery;
import dev.ngb.app.profile.application.dto.ProfileBrief;
import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.dto.ProfileMetadataResponse;
import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.dto.ProfileSummary;
import dev.ngb.infrastructure.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProfileGraphQlController {

    private final ProfileQueryService profileQueryService;

    @QueryMapping
    public ProfileSummary myProfile() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.getMyProfile(accountId);
    }

    @QueryMapping
    public ProfileSummary profile(@Argument String username) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.getProfileByUsername(username, Optional.of(accountId));
    }

    @QueryMapping
    public List<ProfileBrief> followers(
            @Argument String username,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.listFollowers(username, Optional.of(accountId), PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<ProfileBrief> following(
            @Argument String username,
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.listFollowing(username, Optional.of(accountId), PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<FollowRequestResponse> pendingFollowRequests(
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.listPendingFollowRequests(accountId, PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<ProfileLinkResponse> profileLinks(@Argument String username) {
        return profileQueryService.listProfileLinks(username);
    }

    @QueryMapping
    public List<ProfileMetadataResponse> myMetadata() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.listMetadata(accountId);
    }

    @QueryMapping
    public ProfileSettingResponse mySettings() {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.getSettings(accountId);
    }

    @QueryMapping
    public List<ProfileBrief> blockedProfiles(
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.listBlocked(accountId, PageQuery.of(limit, offset));
    }

    @QueryMapping
    public List<ProfileBrief> mutedProfiles(
            @Argument Integer limit,
            @Argument Integer offset
    ) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return profileQueryService.listMuted(accountId, PageQuery.of(limit, offset));
    }
}
