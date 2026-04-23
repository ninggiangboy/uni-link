package dev.ngb.domain.identity.service;

import dev.ngb.domain.identity.model.session.AccountSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResetDomainServiceTest {

    private final PasswordResetDomainService service = new PasswordResetDomainService();

    @Test
    @DisplayName("Revoke all active sessions")
    void revokeActiveSessionsMarksSessionsRevoked() {
        AccountSession one = AccountSession.create(10L, 20L, "h1", "127.0.0.1");
        AccountSession two = AccountSession.create(10L, 21L, "h2", "127.0.0.2");

        service.revokeActiveSessions(List.of(one, two));

        assertThat(one.getIsRevoked()).isTrue();
        assertThat(two.getIsRevoked()).isTrue();
    }
}
