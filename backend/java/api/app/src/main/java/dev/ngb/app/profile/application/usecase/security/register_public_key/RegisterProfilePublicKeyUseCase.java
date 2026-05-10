package dev.ngb.app.profile.application.usecase.security.register_public_key;

import dev.ngb.app.profile.application.usecase.security.register_public_key.dto.RegisterProfilePublicKeyRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;

/*
 * Stores the user's X25519 ECDH public key on their profile so other users may
 * encrypt messages addressed to them. The key is stored verbatim (Base64-encoded
 * by the client) — server never inspects its bytes.
 */
@RequiredArgsConstructor
public class RegisterProfilePublicKeyUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;

    public void execute(Long accountId, RegisterProfilePublicKeyRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);
        profile.registerPublicKey(request.publicKey());
        profileRepository.save(profile);
    }
}
