package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.usecase.add_profile_link.dto.AddProfileLinkRequest;
import dev.ngb.app.profile.application.usecase.update_profile_link.dto.UpdateProfileLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/profiles")
public interface ProfileLinkEndpoint {

    @Operation(summary = "Add an external link to the current account's profile")
    @PostMapping("/me/links")
    ResponseEntity<ProfileLinkResponse> addLink(
            @RequestBody AddProfileLinkRequest request,
            @AuthenticationPrincipal Jwt jwt
    );

    @Operation(summary = "Update an external link on the current account's profile")
    @PatchMapping("/me/links/{linkUuid}")
    ResponseEntity<ProfileLinkResponse> updateLink(
            @PathVariable String linkUuid,
            @RequestBody UpdateProfileLinkRequest request,
            @AuthenticationPrincipal Jwt jwt
    );

    @Operation(summary = "Remove an external link from the current account's profile")
    @DeleteMapping("/me/links/{linkUuid}")
    ResponseEntity<Void> removeLink(
            @PathVariable String linkUuid,
            @AuthenticationPrincipal Jwt jwt
    );
}
