package dev.ngb.app.profile.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Profiles", description = "Profile management for authenticated accounts")
@RequestMapping("/profiles")
public interface ProfileRelationshipEndpoint {

    @Operation(summary = "Block a profile (auto-removes any follow edges and pending requests)")
    @PostMapping("/{username}/block")
    ResponseEntity<Void> block(
            @PathVariable String username
    );

    @Operation(summary = "Unblock a previously-blocked profile")
    @DeleteMapping("/{username}/block")
    ResponseEntity<Void> unblock(
            @PathVariable String username
    );

    @Operation(summary = "Mute a profile (suppresses notifications without affecting follow state)")
    @PostMapping("/{username}/mute")
    ResponseEntity<Void> mute(
            @PathVariable String username
    );

    @Operation(summary = "Unmute a previously-muted profile")
    @DeleteMapping("/{username}/mute")
    ResponseEntity<Void> unmute(
            @PathVariable String username
    );
}
