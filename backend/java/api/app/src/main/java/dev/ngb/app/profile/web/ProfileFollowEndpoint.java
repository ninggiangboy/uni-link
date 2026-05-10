package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.usecase.social.follow_profile.dto.FollowResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/profiles")
public interface ProfileFollowEndpoint {

    @Operation(summary = "Follow a profile (creates a pending request for PRIVATE profiles)")
    @PostMapping("/{username}/follow")
    ResponseEntity<FollowResponse> follow(
            @PathVariable String username
    );

    @Operation(summary = "Unfollow a profile (also cancels a pending follow request, if any)")
    @DeleteMapping("/{username}/follow")
    ResponseEntity<Void> unfollow(
            @PathVariable String username
    );

    @Operation(summary = "Remove a follower from the current account's profile")
    @DeleteMapping("/me/followers/{username}")
    ResponseEntity<Void> removeFollower(
            @PathVariable String username
    );

    @Operation(summary = "Approve a pending follow request")
    @PostMapping("/me/follow-requests/{requestUuid}/approve")
    ResponseEntity<Void> approveFollowRequest(
            @PathVariable String requestUuid
    );

    @Operation(summary = "Reject a pending follow request")
    @PostMapping("/me/follow-requests/{requestUuid}/reject")
    ResponseEntity<Void> rejectFollowRequest(
            @PathVariable String requestUuid
    );
}
