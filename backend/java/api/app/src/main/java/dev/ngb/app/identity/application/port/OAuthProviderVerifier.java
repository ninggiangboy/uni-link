package dev.ngb.app.identity.application.port;

import dev.ngb.domain.identity.model.auth.AuthProvider;

public interface OAuthProviderVerifier {

    OAuthUserInfo verify(AuthProvider provider, String providerToken);

    record OAuthUserInfo(String email, String providerAccountId) {}
}
