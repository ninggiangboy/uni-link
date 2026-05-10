package dev.ngb.app.profile.application.usecase.profile.update_profile_avatar;

import dev.ngb.app.profile.application.usecase.profile.update_profile_avatar.dto.UpdateProfileAvatarRequest;
import dev.ngb.app.shared.public_api.AttachmentPublicApi;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.profile.ProfileMedia;
import dev.ngb.domain.profile.model.profile.ProfileMediaType;
import dev.ngb.domain.profile.repository.ProfileMediaRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

/*
 * Validates that the supplied attachment is owned by the calling account and uploaded,
 * then stores the resolved URL on the Profile aggregate and records a ProfileMedia row.
 */
@RequiredArgsConstructor
public class UpdateProfileAvatarUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileMediaRepository profileMediaRepository;
    private final AttachmentPublicApi attachmentPublicApi;

    public void execute(Long accountId, UpdateProfileAvatarRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        String url = attachmentPublicApi.resolveAvailableUrl(request.attachmentUuid(), accountId)
                .orElseThrow(ProfileError.INVALID_ATTACHMENT::exception);

        profile.setAvatarUrl(url);
        profileRepository.save(profile);
        profileMediaRepository.save(ProfileMedia.create(profile.getId(), ProfileMediaType.AVATAR, url, null));
    }
}
