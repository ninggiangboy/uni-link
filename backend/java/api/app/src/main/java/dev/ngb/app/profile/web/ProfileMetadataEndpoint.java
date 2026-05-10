package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileMetadataResponse;
import dev.ngb.app.profile.application.usecase.upsert_profile_metadata.dto.UpsertProfileMetadataRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/profiles/me/metadata")
public interface ProfileMetadataEndpoint {

    @Operation(summary = "Set or update a metadata key on the current account's profile")
    @PutMapping("/{key}")
    ResponseEntity<ProfileMetadataResponse> upsertMetadata(
            @PathVariable String key,
            @RequestBody UpsertProfileMetadataRequest request
    );

    @Operation(summary = "Remove a metadata key from the current account's profile")
    @DeleteMapping("/{key}")
    ResponseEntity<Void> removeMetadata(
            @PathVariable String key
    );
}
