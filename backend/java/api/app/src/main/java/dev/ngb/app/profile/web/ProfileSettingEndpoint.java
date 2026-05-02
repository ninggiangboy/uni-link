package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.usecase.update_profile_setting.dto.UpdateProfileSettingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/profiles/me/settings")
public interface ProfileSettingEndpoint {

    @Operation(summary = "Update current account's profile interaction settings (partial)")
    @PatchMapping
    ResponseEntity<ProfileSettingResponse> updateSettings(
            @RequestBody UpdateProfileSettingRequest request,
            @AuthenticationPrincipal Jwt jwt
    );
}
