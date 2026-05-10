package dev.ngb.app.profile.application.usecase.profile.change_username;

import dev.ngb.app.profile.application.usecase.profile.change_username.dto.ChangeUsernameRequest;
import dev.ngb.application.UseCaseService;
import dev.ngb.domain.profile.error.ProfileError;
import dev.ngb.domain.profile.model.profile.Profile;
import dev.ngb.domain.profile.model.username.ProfileUsername;
import dev.ngb.domain.profile.repository.ProfileRepository;
import dev.ngb.domain.profile.repository.ProfileUsernameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

/*
 * Renames a profile's username:
 *   - Rejects no-op renames and duplicates already taken by other profiles.
 *   - Demotes the previous current ProfileUsername to historical (isCurrent=false).
 *   - Persists a new ProfileUsername row marked as current.
 *
 * Username reclaim grace period (FR-02.2.3) is intentionally out of scope here;
 * old usernames are immediately freed for re-use by other accounts. The history
 * row remains so audit / redirect support can be added later.
 */
@Slf4j
@RequiredArgsConstructor
public class ChangeUsernameUseCase implements UseCaseService {

    private final ProfileRepository profileRepository;
    private final ProfileUsernameRepository profileUsernameRepository;

    public void execute(Long accountId, ChangeUsernameRequest request) {
        Profile profile = profileRepository.findByAccountId(accountId)
                .orElseThrow(ProfileError.PROFILE_NOT_FOUND::exception);

        String newUsername = request.username();
        if (newUsername.equalsIgnoreCase(profile.getUsername())) {
            throw ProfileError.USERNAME_UNCHANGED.exception();
        }

        profileUsernameRepository.findCurrentByProfileId(profile.getId()).ifPresent(current -> {
            current.markHistorical();
            profileUsernameRepository.save(current);
        });

        profile.changeUsername(newUsername);
        try {
            profileRepository.save(profile);
        } catch (DataIntegrityViolationException e) {
            log.warn("Change username race detected accountId={}, username={}", accountId, newUsername);
            throw ProfileError.USERNAME_ALREADY_EXISTS.exception();
        }
        profileUsernameRepository.save(ProfileUsername.createCurrent(profile.getId(), newUsername));

        log.info("Username changed profileId={}, accountId={}, newUsername={}",
                profile.getId(), accountId, newUsername);
    }
}
