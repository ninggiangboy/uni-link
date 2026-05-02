package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.FollowResponse;
import dev.ngb.app.profile.application.usecase.approve_follow_request.ApproveFollowRequestUseCase;
import dev.ngb.app.profile.application.usecase.follow_profile.FollowProfileUseCase;
import dev.ngb.app.profile.application.usecase.reject_follow_request.RejectFollowRequestUseCase;
import dev.ngb.app.profile.application.usecase.remove_follower.RemoveFollowerUseCase;
import dev.ngb.app.profile.application.usecase.unfollow_profile.UnfollowProfileUseCase;
import dev.ngb.infrastructure.web.ResourceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileFollowResource implements ProfileFollowEndpoint {

    private final FollowProfileUseCase followProfileUseCase;
    private final UnfollowProfileUseCase unfollowProfileUseCase;
    private final RemoveFollowerUseCase removeFollowerUseCase;
    private final ApproveFollowRequestUseCase approveFollowRequestUseCase;
    private final RejectFollowRequestUseCase rejectFollowRequestUseCase;

    @Override
    @Transactional
    public ResponseEntity<FollowResponse> follow(String username, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        return ResourceResponse.ok(followProfileUseCase.execute(accountId, username));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> unfollow(String username, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        unfollowProfileUseCase.execute(accountId, username);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> removeFollower(String username, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        removeFollowerUseCase.execute(accountId, username);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> approveFollowRequest(String requestUuid, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        approveFollowRequestUseCase.execute(accountId, requestUuid);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> rejectFollowRequest(String requestUuid, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        rejectFollowRequestUseCase.execute(accountId, requestUuid);
        return ResourceResponse.noContent();
    }
}
