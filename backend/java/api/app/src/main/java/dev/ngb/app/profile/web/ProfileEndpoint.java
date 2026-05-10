package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileSummary;
import dev.ngb.app.profile.application.usecase.change_username.dto.ChangeUsernameRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.profile.application.usecase.register_public_key.dto.RegisterProfilePublicKeyRequest;
import dev.ngb.app.profile.application.usecase.update_profile.dto.UpdateProfileRequest;
import dev.ngb.app.profile.application.usecase.update_profile_avatar.dto.UpdateProfileAvatarRequest;
import dev.ngb.app.profile.application.usecase.update_profile_banner.dto.UpdateProfileBannerRequest;
import dev.ngb.app.profile.application.usecase.update_profile_visibility.dto.UpdateProfileVisibilityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/profiles")
public interface ProfileEndpoint {

    @Operation(summary = "Create a profile for the current account")
    @PostMapping
    ResponseEntity<CreateProfileResponse> createProfile(
            @RequestBody CreateProfileRequest request
    );

    @Operation(summary = "Update the current account's profile (display name, bio, website, location)")
    @PatchMapping("/me")
    ResponseEntity<ProfileSummary> updateProfile(
            @RequestBody UpdateProfileRequest request
    );

    @Operation(summary = "Update profile visibility")
    @PatchMapping("/me/visibility")
    ResponseEntity<Void> updateVisibility(
            @RequestBody UpdateProfileVisibilityRequest request
    );

    @Operation(summary = "Change the current account's username")
    @PatchMapping("/me/username")
    ResponseEntity<Void> changeUsername(
            @RequestBody ChangeUsernameRequest request
    );

    @Operation(summary = "Update profile avatar from a previously uploaded attachment")
    @PutMapping("/me/avatar")
    ResponseEntity<Void> updateAvatar(
            @RequestBody UpdateProfileAvatarRequest request
    );

    @Operation(summary = "Update profile banner from a previously uploaded attachment")
    @PutMapping("/me/banner")
    ResponseEntity<Void> updateBanner(
            @RequestBody UpdateProfileBannerRequest request
    );

    @Operation(summary = "Register an X25519 ECDH public key for end-to-end encryption")
    @PutMapping("/me/public-key")
    ResponseEntity<Void> registerPublicKey(
            @RequestBody RegisterProfilePublicKeyRequest request
    );
}
