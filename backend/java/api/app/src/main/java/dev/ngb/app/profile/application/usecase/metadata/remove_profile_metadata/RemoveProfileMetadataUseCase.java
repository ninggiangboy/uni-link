package dev.ngb.app.profile.application.usecase.metadata.remove_profile_metadata;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileMetadata;
import dev.ngb.domain.profile.repository.ProfileMetadataRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveProfileMetadataUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileMetadataRepository profileMetadataRepository;

    public void execute(Long accountId, String key) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        ProfileMetadata metadata = profileMetadataRepository.findByProfileIdAndKey(profile.getId(), key)
                .orElseThrow(ProfileError.METADATA_KEY_NOT_FOUND::exception);
        profileMetadataRepository.delete(metadata);
    }
}
