package dev.ngb.app.profile.application.usecase.create_profile;

import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.shared.public_api.IdentityPublicApi;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateProfileUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final IdentityPublicApi identityPublicApi;

    public CreateProfileResponse execute(Long accountId, CreateProfileRequest request) {
        if (!identityPublicApi.isAccountActive(accountId)) {
            throw ProfileError.ACCOUNT_NOT_ACTIVE.exception();
        }

        if (profileRepository.existsByUsername(request.username())) {
            throw ProfileError.USERNAME_ALREADY_EXISTS.exception();
        }

        Profile profile = Profile.createForNewAccount(
                accountId,
                request.username(),
                request.displayName(),
                request.bio(),
                request.visibility()
        );
        Profile savedProfile = profileRepository.save(profile);
        return new CreateProfileResponse(
                savedProfile.getUuid(),
                savedProfile.getUsername(),
                savedProfile.getDisplayName(),
                savedProfile.getVisibility(),
                savedProfile.getCreatedAt()
        );
    }
}
