package dev.ngb.app.profile.application.usecase.upsert_profile_metadata;

import dev.ngb.app.profile.ProfileConstants;
import dev.ngb.app.profile.application.dto.ProfileMetadataResponse;
import dev.ngb.app.profile.application.usecase.upsert_profile_metadata.dto.UpsertProfileMetadataRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileMetadata;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.util.validation.FluentValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpsertProfileMetadataUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileMetadataRepository profileMetadataRepository;

    public ProfileMetadataResponse execute(Long accountId, String key, UpsertProfileMetadataRequest request) {
        validateKey(key);
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        ProfileMetadata metadata = profileMetadataRepository.findByProfileIdAndKey(profile.getId(), key)
                .orElseGet(() -> ProfileMetadata.create(profile.getId(), key, request.value()));
        if (metadata.getId() != null) {
            metadata.updateValue(request.value());
        }
        ProfileMetadata saved = profileMetadataRepository.save(metadata);
        return ProfileMetadataResponse.of(saved);
    }

    private static void validateKey(String key) {
        FluentValidator.of(key)
                .ruleFor("key", k -> k)
                .notNullOrBlank()
                .maxLength(ProfileConstants.MAX_METADATA_KEY_LENGTH)
                .matches("^[A-Za-z0-9_.-]+$")
                .validateAndThrow();
    }
}
