package dev.ngb.domain.group.error;

import dev.ngb.domain.DomainError;
import dev.ngb.domain.DomainErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupError implements DomainError {

    GROUP_NOT_FOUND("Group not found", DomainErrorType.NOT_FOUND),
    GROUP_SLUG_ALREADY_EXISTS("Group slug is already taken", DomainErrorType.CONFLICT),
    GROUP_SUSPENDED("Group has been suspended", DomainErrorType.FORBIDDEN),
    GROUP_ARCHIVED("Group has been archived", DomainErrorType.FORBIDDEN),
    ALREADY_A_MEMBER("Profile is already a member of this group", DomainErrorType.CONFLICT),
    NOT_A_MEMBER("Profile is not a member of this group", DomainErrorType.NOT_FOUND),
    MEMBER_BANNED("Profile has been banned from this group", DomainErrorType.FORBIDDEN),
    INSUFFICIENT_ROLE("Insufficient role to perform this action", DomainErrorType.FORBIDDEN),
    INVITATION_NOT_FOUND("Invitation not found", DomainErrorType.NOT_FOUND),
    INVITATION_ALREADY_PENDING("A pending invitation already exists for this profile", DomainErrorType.CONFLICT),
    INVITATION_EXPIRED("Invitation has expired", DomainErrorType.VALIDATION),
    MEMBER_REQUEST_NOT_FOUND("Join request not found", DomainErrorType.NOT_FOUND),
    MEMBER_REQUEST_ALREADY_PENDING("A pending join request already exists for this profile", DomainErrorType.CONFLICT),
    GROUP_RULE_NOT_FOUND("Group rule not found", DomainErrorType.NOT_FOUND);

    private final String message;
    private final DomainErrorType type;
}
