package dev.ngb.domain.identity.service;

import dev.ngb.domain.DomainService;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;

import java.util.Optional;

public class OAuthAccountDomainService implements DomainService {

    public OAuthAccountResolution decideOAuthAccountLinking(Optional<Account> existingAccount, String email, boolean providerLinked) {
        if (existingAccount.isEmpty()) {
            return new OAuthAccountResolution(Account.createFromOAuth(email), true, true);
        }

        Account account = existingAccount.get();
        if (!account.isActive()) {
            throw AccountError.ACCOUNT_NOT_ACTIVE.exception();
        }

        return new OAuthAccountResolution(account, false, !providerLinked);
    }

    public record OAuthAccountResolution(Account account, boolean newAccount, boolean shouldLinkProvider) {
    }
}
