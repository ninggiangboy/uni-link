package dev.ngb.app.identity.infrastructure;

import dev.ngb.app.identity.application.port.OAuthProviderVerifier;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import org.springframework.stereotype.Component;

/**
 * Placeholder for OAuth provider verification.
 * Replace with real provider SDK integrations (Google, Apple, etc.) in production.
 */
@Component
public class StubOAuthProviderVerifier implements OAuthProviderVerifier {

    @Override
    public OAuthUserInfo verify(AuthProvider provider, String providerToken) {
        throw new UnsupportedOperationException(
                "OAuth verification for " + provider + " is not yet implemented"
        );
    }
}
