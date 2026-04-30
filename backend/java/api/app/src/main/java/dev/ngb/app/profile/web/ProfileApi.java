package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/api/profiles")
public interface ProfileApi {

    @Operation(summary = "Create a profile for the current account")
    @PostMapping
    ResponseEntity<CreateProfileResponse> createProfile(
            @RequestBody CreateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt
    );
}
