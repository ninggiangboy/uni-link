package dev.ngb.domain.identity.service;

import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OAuthAccountDomainServiceTest {

    private final OAuthAccountDomainService service = new OAuthAccountDomainService();

    @Test
    @DisplayName("Missing account creates a new OAuth account")
    void resolveWhenMissingCreatesNewAccount() {
        var result = service.decideOAuthAccountLinking(Optional.empty(), "user@test.com", false);

        assertThat(result.newAccount()).isTrue();
        assertThat(result.shouldLinkProvider()).isTrue();
        assertThat(result.account().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("Inactive account is rejected")
    void resolveWhenInactiveThrows() {
        Account pending = account(AccountStatus.PENDING);

        DomainException ex = assertThrows(DomainException.class, () -> service.decideOAuthAccountLinking(Optional.of(pending), "user@test.com", false));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_ACTIVE);
    }

    @Test
    @DisplayName("Active account links provider only when missing")
    void resolveWhenActiveReturnsLinkDecision() {
        Account active = account(AccountStatus.ACTIVE);

        var notLinked = service.decideOAuthAccountLinking(Optional.of(active), "user@test.com", false);
        var linked = service.decideOAuthAccountLinking(Optional.of(active), "user@test.com", true);

        assertThat(notLinked.newAccount()).isFalse();
        assertThat(notLinked.shouldLinkProvider()).isTrue();
        assertThat(linked.shouldLinkProvider()).isFalse();
    }

    private static Account account(AccountStatus status) {
        return Account.reconstruct(
                1L,
                "uuid-1",
                null,
                Instant.now(),
                null,
                Instant.now(),
                "user@test.com",
                null,
                "hash",
                status,
                status == AccountStatus.ACTIVE,
                false,
                false,
                null,
                null
        );
    }
}
