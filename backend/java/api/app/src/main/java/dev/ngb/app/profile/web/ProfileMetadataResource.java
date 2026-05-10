package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileMetadataResponse;
import dev.ngb.app.profile.application.usecase.remove_profile_metadata.RemoveProfileMetadataUseCase;
import dev.ngb.app.profile.application.usecase.upsert_profile_metadata.UpsertProfileMetadataUseCase;
import dev.ngb.app.profile.application.usecase.upsert_profile_metadata.dto.UpsertProfileMetadataRequest;
import dev.ngb.infrastructure.web.ResourceResponse;
import dev.ngb.infrastructure.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileMetadataResource implements ProfileMetadataEndpoint {

    private final UpsertProfileMetadataUseCase upsertProfileMetadataUseCase;
    private final RemoveProfileMetadataUseCase removeProfileMetadataUseCase;

    @Override
    @Transactional
    public ResponseEntity<ProfileMetadataResponse> upsertMetadata(String key, UpsertProfileMetadataRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return ResourceResponse.ok(upsertProfileMetadataUseCase.execute(accountId, key, request));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> removeMetadata(String key) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        removeProfileMetadataUseCase.execute(accountId, key);
        return ResourceResponse.noContent();
    }
}
