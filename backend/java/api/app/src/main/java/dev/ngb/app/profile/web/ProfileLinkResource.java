package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileLinkResponse;
import dev.ngb.app.profile.application.usecase.add_profile_link.AddProfileLinkUseCase;
import dev.ngb.app.profile.application.usecase.add_profile_link.dto.AddProfileLinkRequest;
import dev.ngb.app.profile.application.usecase.remove_profile_link.RemoveProfileLinkUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_link.UpdateProfileLinkUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_link.dto.UpdateProfileLinkRequest;
import dev.ngb.infrastructure.web.ResourceResponse;
import dev.ngb.infrastructure.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileLinkResource implements ProfileLinkEndpoint {

    private final AddProfileLinkUseCase addProfileLinkUseCase;
    private final UpdateProfileLinkUseCase updateProfileLinkUseCase;
    private final RemoveProfileLinkUseCase removeProfileLinkUseCase;

    @Override
    @Transactional
    public ResponseEntity<ProfileLinkResponse> addLink(AddProfileLinkRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return ResourceResponse.created(addProfileLinkUseCase.execute(accountId, request));
    }

    @Override
    @Transactional
    public ResponseEntity<ProfileLinkResponse> updateLink(String linkUuid, UpdateProfileLinkRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return ResourceResponse.ok(updateProfileLinkUseCase.execute(accountId, linkUuid, request));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> removeLink(String linkUuid) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        removeProfileLinkUseCase.execute(accountId, linkUuid);
        return ResourceResponse.noContent();
    }
}
