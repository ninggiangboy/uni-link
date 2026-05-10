package dev.ngb.app.profile.web;

import dev.ngb.app.profile.application.usecase.social.block_profile.BlockProfileUseCase;
import dev.ngb.app.profile.application.usecase.social.mute_profile.MuteProfileUseCase;
import dev.ngb.app.profile.application.usecase.social.unblock_profile.UnblockProfileUseCase;
import dev.ngb.app.profile.application.usecase.social.unmute_profile.UnmuteProfileUseCase;
import dev.ngb.infrastructure.web.ResourceResponse;
import dev.ngb.infrastructure.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileRelationshipResource implements ProfileRelationshipEndpoint {

    private final BlockProfileUseCase blockProfileUseCase;
    private final UnblockProfileUseCase unblockProfileUseCase;
    private final MuteProfileUseCase muteProfileUseCase;
    private final UnmuteProfileUseCase unmuteProfileUseCase;

    @Override
    @Transactional
    public ResponseEntity<Void> block(String username) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        blockProfileUseCase.execute(accountId, username);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> unblock(String username) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        unblockProfileUseCase.execute(accountId, username);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> mute(String username) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        muteProfileUseCase.execute(accountId, username);
        return ResourceResponse.noContent();
    }

    @Override
    @Transactional
    public ResponseEntity<Void> unmute(String username) {
        Long accountId = SecurityUtils.getCurrentAccountId();
        unmuteProfileUseCase.execute(accountId, username);
        return ResourceResponse.noContent();
    }
}
