package dev.ngb.app.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ngb.app.identity.support.IdentityIntegrationTestConfig;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.neo4j.Neo4jContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(IdentityIntegrationTestConfig.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected ObjectMapper objectMapper;

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("uni-link_test")
            .withUsername("postgres")
            .withPassword("postgres");

    /**
     * Neo4j for graph-backed profile relationships ({@code FOLLOWS}, {@code BLOCKS}, {@code MUTES}, …).
     * Uses the same credentials Spring Boot expects when {@link Neo4jContainer#withAdminPassword} is set.
     */
    static final Neo4jContainer NEO4J = new Neo4jContainer(DockerImageName.parse("neo4j:5-community"))
            .withAdminPassword("uni-link-test-neo4j");

    static {
        POSTGRES.start();
        NEO4J.start();
    }

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    protected final RestTemplate restTemplate = createRestTemplate();

    @DynamicPropertySource
    static void configureTestcontainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.neo4j.uri", NEO4J::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", NEO4J::getAdminPassword);
    }

    protected String baseUrl() {
        return "http://localhost:" + port + contextPath;
    }

    public static RestTemplate createRestTemplate() {
        var template = new RestTemplate(new JdkClientHttpRequestFactory());
        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(@NonNull ClientHttpResponse response) {
                return false;
            }
        });
        return template;
    }

    public static HttpHeaders jsonRequestHeaders() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    public static HttpHeaders bearerHeaders(String accessToken) {
        var headers = jsonRequestHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }
}
