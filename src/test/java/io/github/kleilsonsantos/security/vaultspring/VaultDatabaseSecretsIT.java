package io.github.kleilsonsantos.security.vaultspring;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: Spring Cloud Vault Database Secrets Engine → PostgreSQL (ADR-0005 Phase 1).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"vault-db-it", "vault"})
@Testcontainers(disabledWithoutDocker = true)
class VaultDatabaseSecretsIT {

    private static final String VAULT_ROOT_TOKEN = "root";

    private static final String VAULT_DB_ROLE = "vaultspring-app";

    private static final Network NETWORK = Network.newNetwork();

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
            .withNetwork(NETWORK)
            .withNetworkAliases("postgres");

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> VAULT = new GenericContainer<>(DockerImageName.parse("hashicorp/vault:1.15"))
            .withNetwork(NETWORK)
            .withExposedPorts(8200)
            .withCommand("server", "-dev", "-dev-root-token-id=" + VAULT_ROOT_TOKEN,
                    "-dev-listen-address=0.0.0.0:8200")
            .waitingFor(Wait.forHttp("/v1/sys/health").forPort(8200));

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.vault.uri",
                () -> "http://" + VAULT.getHost() + ":" + VAULT.getMappedPort(8200));
        registry.add("spring.cloud.vault.token", () -> VAULT_ROOT_TOKEN);
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("VAULT_DB_ROLE", () -> VAULT_DB_ROLE);
    }

    @BeforeAll
    static void configureVaultDatabaseEngine() throws Exception {
        enableDatabaseEngine();
        configureDatabaseConnection();
        configureDatabaseRole();
    }

    @Test
    void contextLoadsWithDynamicDatabaseCredentials() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String dbUser = connection.getMetaData().getUserName();
            assertThat(dbUser).isNotBlank();
            assertThat(dbUser).isNotEqualTo(POSTGRES.getUsername());
        }
    }

    @Test
    void healthEndpointReportsUp() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"status\":\"UP\"");
    }

    private static void enableDatabaseEngine() throws Exception {
        vaultPost("/v1/sys/mounts/database", "{\"type\":\"database\"}");
    }

    private static void configureDatabaseConnection() throws Exception {
        String body = """
                {
                  "plugin_name": "postgresql-database-plugin",
                  "allowed_roles": "%s",
                  "connection_url": "postgresql://{{username}}:{{password}}@postgres:5432/%s?sslmode=disable",
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(VAULT_DB_ROLE, POSTGRES.getDatabaseName(),
                POSTGRES.getUsername(), POSTGRES.getPassword());
        vaultPost("/v1/database/config/vaultspring-postgresql", body);
    }

    private static void configureDatabaseRole() throws Exception {
        String body = """
                {
                  "db_name": "vaultspring-postgresql",
                  "creation_statements": "CREATE ROLE \\"{{name}}\\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \\"{{name}}\\"; GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO \\"{{name}}\\";",
                  "default_ttl": "1h",
                  "max_ttl": "24h"
                }
                """;
        vaultPost("/v1/database/roles/" + VAULT_DB_ROLE, body);
    }

    private static void vaultPost(String path, String jsonBody) throws Exception {
        URI uri = URI.create(vaultUri() + path);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("X-Vault-Token", VAULT_ROOT_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400 && !alreadyExists(response.body())) {
            throw new IllegalStateException(
                    "Vault POST " + path + " failed (" + response.statusCode() + "): " + response.body());
        }
    }

    private static boolean alreadyExists(String body) {
        return body != null && (body.contains("path is already in use")
                || body.contains("existing mount"));
    }

    private static String vaultUri() {
        return "http://" + VAULT.getHost() + ":" + VAULT.getMappedPort(8200);
    }
}
