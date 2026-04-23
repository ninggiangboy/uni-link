package dev.ngb.domain.identity.service;

import dev.ngb.domain.identity.model.session.AccountSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionRotationDomainServiceTest {

    private final SessionRotationDomainService service = new SessionRotationDomainService();

    @Test
    @DisplayName("Rotation revokes old session and creates replacement")
    void rotateRevokesAndCreatesNewSession() {
        AccountSession current = AccountSession.create(10L, 20L, "h1", "127.0.0.1");

        var result = service.rotate(current, "h2");

        assertThat(result.revokedSession().getIsRevoked()).isTrue();
        assertThat(result.newSession().getAccountId()).isEqualTo(10L);
        assertThat(result.newSession().getDeviceId()).isEqualTo(20L);
        assertThat(result.newSession().getTokenHash()).isEqualTo("h2");
        assertThat(result.newSession().getIpAddress()).isEqualTo("127.0.0.1");
    }
}
