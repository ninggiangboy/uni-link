package dev.ngb.domain.profile.error;

import dev.ngb.domain.DomainError;
import dev.ngb.domain.DomainErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileError implements DomainError {

    ACCOUNT_NOT_ACTIVE("Account is not active", DomainErrorType.FORBIDDEN),
    PROFILE_NOT_FOUND("Profile not found", DomainErrorType.NOT_FOUND),
    PROFILE_ALREADY_EXISTS_FOR_ACCOUNT("Account already has a profile", DomainErrorType.CONFLICT),
    USERNAME_ALREADY_EXISTS("Username is already taken", DomainErrorType.CONFLICT),
    USERNAME_UNCHANGED("New username matches the current username", DomainErrorType.CONFLICT),

    INVALID_ATTACHMENT("Attachment is missing, not owned by the account, or not yet available", DomainErrorType.VALIDATION),

    LINK_NOT_FOUND("Profile link not found", DomainErrorType.NOT_FOUND),
    MAX_LINKS_REACHED("Profile already has the maximum number of links", DomainErrorType.CONFLICT),
    METADATA_KEY_NOT_FOUND("Metadata key not found on this profile", DomainErrorType.NOT_FOUND),

    CANNOT_FOLLOW_SELF("A profile cannot follow itself", DomainErrorType.VALIDATION),
    CANNOT_BLOCK_SELF("A profile cannot block itself", DomainErrorType.VALIDATION),
    CANNOT_MUTE_SELF("A profile cannot mute itself", DomainErrorType.VALIDATION),

    ALREADY_FOLLOWING("You already follow this profile", DomainErrorType.CONFLICT),
    NOT_FOLLOWING("You do not follow this profile", DomainErrorType.NOT_FOUND),
    NOT_FOLLOWED_BY("This profile does not follow you", DomainErrorType.NOT_FOUND),
    BLOCKED_BY_TARGET("You cannot interact with this profile because they have blocked you", DomainErrorType.FORBIDDEN),
    TARGET_BLOCKED("You have blocked this profile; unblock to interact", DomainErrorType.FORBIDDEN),
    ALREADY_BLOCKED("You have already blocked this profile", DomainErrorType.CONFLICT),
    NOT_BLOCKED("You have not blocked this profile", DomainErrorType.NOT_FOUND),
    ALREADY_MUTED("You have already muted this profile", DomainErrorType.CONFLICT),
    NOT_MUTED("You have not muted this profile", DomainErrorType.NOT_FOUND),

    FOLLOW_REQUEST_NOT_FOUND("Follow request not found", DomainErrorType.NOT_FOUND),
    FOLLOW_REQUEST_ALREADY_PENDING("A follow request is already pending for this profile", DomainErrorType.CONFLICT);

    private final String message;
    private final DomainErrorType type;
}
