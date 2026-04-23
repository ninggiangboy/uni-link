package dev.ngb.domain.identity.service;

import dev.ngb.domain.DomainService;
import dev.ngb.domain.identity.model.session.AccountSession;

public class SessionRotationDomainService implements DomainService {

    public SessionRotation rotate(AccountSession currentSession, String newTokenHash) {
        currentSession.revoke();
        AccountSession newSession = AccountSession.create(
                currentSession.getAccountId(),
                currentSession.getDeviceId(),
                newTokenHash,
                currentSession.getIpAddress()
        );

        return new SessionRotation(currentSession, newSession);
    }

    public record SessionRotation(AccountSession revokedSession, AccountSession newSession) {
    }
}
