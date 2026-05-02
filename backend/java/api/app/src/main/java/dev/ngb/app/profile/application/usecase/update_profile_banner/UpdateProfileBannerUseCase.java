package dev.ngb.app.profile.application.usecase.update_profile_banner;

import dev.ngb.app.profile.application.usecase.update_profile_banner.dto.UpdateProfileBannerRequest;
import dev.ngb.app.shared.public_api.AttachmentPublicApi;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileMedia;
import dev.ngb.domain.profile.model.profile.ProfileMediaType;
import dev.ngb.domain.profile.repository.ProfileMediaRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateProfileBannerUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileMediaRepository profileMediaRepository;
    private final AttachmentPublicApi attachmentPublicApi;

    public void execute(Long accountId, UpdateProfileBannerRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        String url = attachmentPublicApi.resolveAvailableUrl(request.attachmentUuid(), accountId)
                .orElseThrow(ProfileError.INVALID_ATTACHMENT::exception);

        profile.setBannerUrl(url);
        profileRepository.save(profile);
        profileMediaRepository.save(ProfileMedia.create(profile.getId(), ProfileMediaType.BANNER, url, null));
    }
}
