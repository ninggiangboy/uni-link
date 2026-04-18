package dev.ngb.app.identity.application;

import dev.ngb.app.common.public_api.IdentityPublicApi;
import dev.ngb.application.PublicApi;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class IdentityPublicApiImpl implements IdentityPublicApi, PublicApi {

    private final AccountRepository accountRepository;

    @Override
    public boolean isAccountActive(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        return account.map(Account::isActive).orElse(false);
    }
}

