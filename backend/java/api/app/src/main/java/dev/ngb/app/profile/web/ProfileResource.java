package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.usecase.create_profile.CreateProfileUseCase;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileRequest;
import dev.ngb.app.profile.application.usecase.create_profile.dto.CreateProfileResponse;
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

    @Override
    @Transactional
    public ResponseEntity<CreateProfileResponse> createProfile(CreateProfileRequest request, Jwt jwt) {
        Long accountId = jwt.getClaim("account_id");
        CreateProfileResponse response = createProfileUseCase.execute(accountId, request);
        return ResourceResponse.created(response);
    }
}
