package dev.ngb.app.identity.support;

import dev.ngb.app.identity.application.port.OAuthProviderVerifier;
import dev.ngb.app.identity.application.port.OtpSender;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.domain.identity.model.auth.AuthProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class IdentityIntegrationTestConfig {

    /**
     * Kafka is disabled in {@code application-test.yml}; provides a no-op publisher so API tests start.
     */
    @Bean
    @Primary
    public EventPublisher testEventPublisher() {
        return _ -> {
        };
    }

    @Bean
    @Primary
    public OtpSender testOtpSender() {
        return new TestOtpSender();
    }

    /**
     * Overrides production {@link dev.ngb.app.identity.infrastructure.StubOAuthProviderVerifier} so
     * HTTP integration tests can complete OAuth login with a fixed provider token.
     */
    @Bean
    @Primary
    public OAuthProviderVerifier integrationOAuthProviderVerifier() {
        return (AuthProvider provider, String providerToken) -> {
            if ("integration-oauth-valid".equals(providerToken)) {
                return new OAuthProviderVerifier.OAuthUserInfo("oauth.integration@test.com", "integration-sub");
            }
            throw new IllegalArgumentException("invalid provider token for integration test");
        };
    }
}
