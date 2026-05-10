package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.dto.ProfileSettingResponse;
import dev.ngb.app.profile.application.usecase.update_profile_setting.UpdateProfileSettingUseCase;
import dev.ngb.app.profile.application.usecase.update_profile_setting.dto.UpdateProfileSettingRequest;
import dev.ngb.infrastructure.web.ResourceResponse;
import dev.ngb.infrastructure.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileSettingResource implements ProfileSettingEndpoint {

    private final UpdateProfileSettingUseCase updateProfileSettingUseCase;

    @Override
    @Transactional
    public ResponseEntity<ProfileSettingResponse> updateSettings(UpdateProfileSettingRequest request) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        return ResourceResponse.ok(updateProfileSettingUseCase.execute(accountId, request));
    }
}
