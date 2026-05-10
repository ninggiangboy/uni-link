package dev.ngb.app.profile.application.usecase.social.reject_follow_request;

import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.relationship.FollowRequest;
import dev.ngb.domain.profile.repository.FollowRequestRepository;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RejectFollowRequestUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final FollowRequestRepository followRequestRepository;

    public void execute(Long accountId, String requestUuid) {
        Profile owner = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        FollowRequest request = followRequestRepository.findByUuidAndTargetProfileId(requestUuid, owner.getId())
                .orElseThrow(ProfileError.FOLLOW_REQUEST_NOT_FOUND::exception);
        if (!request.isPending()) {
            throw ProfileError.FOLLOW_REQUEST_NOT_FOUND.exception();
        }

        request.reject();
        followRequestRepository.save(request);
        log.info("Follow request rejected requestId={}, requesterId={}, ownerId={}",
                request.getId(), request.getRequesterProfileId(), owner.getId());
    }
}
