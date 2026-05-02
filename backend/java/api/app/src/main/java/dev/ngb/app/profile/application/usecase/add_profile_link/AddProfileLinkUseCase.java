package dev.ngb.app.profile.application.usecase.add_profile_link;

import dev.ngb.app.profile.ProfileConstants;
import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.usecase.add_profile_link.dto.AddProfileLinkRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileLink;
import dev.ngb.domain.profile.repository.ProfileLinkRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddProfileLinkUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileLinkRepository profileLinkRepository;

    public ProfileLinkResponse execute(Long accountId, AddProfileLinkRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        if (profileLinkRepository.countByProfileId(profile.getId()) >= ProfileConstants.MAX_PROFILE_LINKS) {
            throw ProfileError.MAX_LINKS_REACHED.exception();
        }

        ProfileLink saved = profileLinkRepository.save(ProfileLink.create(
                profile.getId(),
                request.type(),
                request.url(),
                request.orderIndex()
        ));
        return ProfileLinkResponse.of(saved);
    }
}
