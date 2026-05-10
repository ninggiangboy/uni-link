package dev.ngb.app.profile.application.usecase.link.update_profile_link;

import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.usecase.link.update_profile_link.dto.UpdateProfileLinkRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateProfileLinkUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileLinkRepository profileLinkRepository;

    public ProfileLinkResponse execute(Long accountId, String linkUuid, UpdateProfileLinkRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        ProfileLink link = profileLinkRepository.findByUuidAndProfileId(linkUuid, profile.getId())
                .orElseThrow(ProfileError.LINK_NOT_FOUND::exception);
        link.update(request.type(), request.url(), request.orderIndex());
        return ProfileLinkResponse.of(profileLinkRepository.save(link));
    }
}
