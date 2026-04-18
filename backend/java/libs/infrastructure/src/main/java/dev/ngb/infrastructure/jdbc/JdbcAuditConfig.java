package dev.ngb.infrastructure.jdbc;

import dev.ngb.application.port.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

import java.util.Optional;

@Configuration
@EnableJdbcAuditing(dateTimeProviderRef = "dateTimeProvider")
public class JdbcAuditConfig {

    @Bean
    public DateTimeProvider dateTimeProvider(TimeProvider timeProvider) {
        return () -> Optional.of(timeProvider.now());
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return Optional::empty;
    }
}
