package dev.ngb.infrastructure.jdbc;

import dev.ngb.constant.PackageConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackages = PackageConstants.BASE_PACKAGE)
public class JdbcRepositoryConfig {
}
