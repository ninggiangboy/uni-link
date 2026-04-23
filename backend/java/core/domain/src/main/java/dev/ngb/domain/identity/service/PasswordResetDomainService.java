package dev.ngb.domain.identity.service;

import dev.ngb.domain.DomainService;
import dev.ngb.domain.identity.model.session.AccountSession;

import java.util.List;

public class PasswordResetDomainService implements DomainService {

    public void revokeActiveSessions(List<AccountSession> activeSessions) {
        activeSessions.forEach(AccountSession::revoke);
    }
}
