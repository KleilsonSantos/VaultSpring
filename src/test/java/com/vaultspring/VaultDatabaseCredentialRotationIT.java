package com.vaultspring;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
 * Integration test: runtime HikariCP rotation after Vault {@code max_lease_ttl} (ADR-0005 Phase 2).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"vault-db-it", "vault"})
@Testcontainers(disabledWithoutDocker = true)
class VaultDatabaseCredentialRotationIT {

    private static final String VAULT_ROOT_TOKEN = "root";

    private static final String VAULT_DB_ROLE = "vaultspring-rotate-it";

    private static final Duration MAX_LEASE = Duration.ofSeconds(5);

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

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.vault.uri",
                () -> "http://" + VAULT.getHost() + ":" + VAULT.getMappedPort(8200));
        registry.add("spring.cloud.vault.token", () -> VAULT_ROOT_TOKEN);
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("VAULT_DB_ROLE", () -> VAULT_DB_ROLE);
        registry.add("spring.cloud.vault.config.lifecycle.min-renewal", () -> "1s");
        registry.add("spring.cloud.vault.config.lifecycle.expiry-threshold", () -> "1s");
    }

    @BeforeAll
    static void configureVaultDatabaseEngine() throws Exception {
        enableDatabaseEngine();
        configureDatabaseConnection();
        configureDatabaseRoleShortTtl();
    }

    @Test
    void rotatesCredentialsAfterMaxLeaseTtl() throws Exception {
        String initialUser = currentDatabaseUser();
        assertThat(initialUser).isNotBlank();

        Duration timeout = MAX_LEASE.plusSeconds(10);
        long deadline = System.nanoTime() + timeout.toNanos();
        String rotatedUser = initialUser;
        while (System.nanoTime() < deadline) {
            rotatedUser = currentDatabaseUser();
            if (!initialUser.equals(rotatedUser)) {
                break;
            }
            Thread.sleep(500);
        }

        assertThat(rotatedUser)
                .as("database user after max_lease_ttl rotation")
                .isNotBlank()
                .isNotEqualTo(initialUser);
    }

    private String currentDatabaseUser() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getUserName();
        }
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

    private static void configureDatabaseRoleShortTtl() throws Exception {
        String body = """
                {
                  "db_name": "vaultspring-postgresql",
                  "creation_statements": "CREATE ROLE \\"{{name}}\\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \\"{{name}}\\"; GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO \\"{{name}}\\";",
                  "default_ttl": "2s",
                  "max_ttl": "5s"
                }
                """;
        vaultPost("/v1/database/roles/" + VAULT_DB_ROLE, body);
    }

    private static void vaultPost(final String path, final String jsonBody) throws Exception {
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

    private static boolean alreadyExists(final String body) {
        return body != null && (body.contains("path is already in use")
                || body.contains("existing mount"));
    }

    private static String vaultUri() {
        return "http://" + VAULT.getHost() + ":" + VAULT.getMappedPort(8200);
    }
}
