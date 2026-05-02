package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileSummary;
import dev.ngb.app.profile.application.usecase.change_username.ChangeUsernameUseCase;
import dev.ngb.app.profile.application.usecase.change_username.dto.ChangeUsernameRequest;
import dev.ngb.app.profile.application.usecase.create_profile.CreateProfileUseCase;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
import dev.ngb.app.profile.application.usecase.register_public_key.RegisterProfilePublicKeyUseCase;
import dev.ngb.app.profile.application.usecase.register_public_key.dto.RegisterProfilePublicKeyRequest;
import dev.ngb.app.profile.application.usecase.update_profile.UpdateProfileUseCase;
import dev.ngb.app.profile.application.usecase.update_profile.dto.UpdateProfileRequest;
import dev.ngb.app.profile.application.usecase.update_profile_avatar.UpdateProfileAvatarUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_avatar.dto.UpdateProfileAvatarRequest;
import dev.ngb.app.profile.application.usecase.update_profile_banner.UpdateProfileBannerUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_banner.dto.UpdateProfileBannerRequest;
import dev.ngb.app.profile.application.usecase.update_profile_visibility.UpdateProfileVisibilityUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_visibility.dto.UpdateProfileVisibilityRequest;
import dev.ngb.infrastructure.web.ResourceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileResource implements ProfileEndpoint {

    private final CreateProfileUseCase createProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UpdateProfileVisibilityUseCase updateProfileVisibilityUseCase;
    private final UpdateProfileAvatarUseCase updateProfileAvatarUseCase;
    private final UpdateProfileBannerUseCase updateProfileBannerUseCase;
    private final RegisterProfilePublicKeyUseCase registerProfilePublicKeyUseCase;
    private final ChangeUsernameUseCase changeUsernameUseCase;

    @Override
    @Transactional
    public ResponseEntity<CreateProfileResponse> createProfile(CreateProfileRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        CreateProfileResponse response = createProfileUseCase.execute(accountId, request);
        return ResourceResponse.created(response);
    }

    @Override
    @Transactional
    public ResponseEntity<ProfileSummary> updateProfile(UpdateProfileRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        return ResourceResponse.ok(updateProfileUseCase.execute(accountId, request));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateVisibility(UpdateProfileVisibilityRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        updateProfileVisibilityUseCase.execute(accountId, request);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> changeUsername(ChangeUsernameRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        changeUsernameUseCase.execute(accountId, request);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateAvatar(UpdateProfileAvatarRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        updateProfileAvatarUseCase.execute(accountId, request);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateBanner(UpdateProfileBannerRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        updateProfileBannerUseCase.execute(accountId, request);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> registerPublicKey(RegisterProfilePublicKeyRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        registerProfilePublicKeyUseCase.execute(accountId, request);
        return ResourceResponse.noContent();
    }
}
