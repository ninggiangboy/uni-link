package dev.ngb.app.profile.application.usecase.remove_profile_link;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveProfileLinkUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileLinkRepository profileLinkRepository;

    public void execute(Long accountId, String linkUuid) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        ProfileLink link = profileLinkRepository.findByUuidAndProfileId(linkUuid, profile.getId())
                .orElseThrow(ProfileError.LINK_NOT_FOUND::exception);
        profileLinkRepository.delete(link);
    }
}
