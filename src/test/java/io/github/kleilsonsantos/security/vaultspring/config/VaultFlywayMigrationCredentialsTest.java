package io.github.kleilsonsantos.security.vaultspring.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link VaultFlywayMigrationCredentials} and the Vault role SQL it depends on.
 */
class VaultFlywayMigrationCredentialsTest {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/users_db";

    /**
     * Vault integration tests leave {@code POSTGRES_USER} unset and must keep Flyway on the pool.
     */
    @Test
    void blankMigrationUserKeepsApplicationDataSource() {
        HikariDataSource pool = pool();
        try {
            FluentConfiguration configuration = new FluentConfiguration();
            configuration.dataSource(pool);

            customizer("", "").customize(configuration);

            assertThat(configuration.getDataSource()).isSameAs(pool);
        } finally {
            pool.close();
        }
    }

    /**
     * Compose and {@code prod-vault} set {@code POSTGRES_USER}. Flyway must not log in as the
     * dynamic role, which cannot {@code CREATE} in {@code public} on PostgreSQL 15.
     */
    @Test
    void staticAdminReplacesFlywayLogin() throws Exception {
        HikariDataSource pool = pool();
        try {
            FluentConfiguration configuration = new FluentConfiguration();
            configuration.dataSource(pool);

            customizer("admin", "adminpass").customize(configuration);

            DriverDataSource migration = (DriverDataSource) configuration.getDataSource();
            assertThat(migration).isNotSameAs(pool);
            assertThat(migration.getUrl()).isEqualTo(JDBC_URL);
            assertThat(migration.getUser()).isEqualTo("admin");
            assertThat(migration.getPassword()).isEqualTo("adminpass");
        } finally {
            pool.close();
        }
    }

    /**
     * A configured admin with no Hikari URL must fail startup instead of migrating as the
     * under-privileged dynamic role.
     */
    @Test
    void missingJdbcUrlFailsFast() {
        HikariDataSource pool = new HikariDataSource();
        pool.setPoolName("vault-flyway-test-empty");
        try {
            FluentConfiguration configuration = new FluentConfiguration();
            configuration.dataSource(pool);

            assertThatThrownBy(() -> customizer("admin", "adminpass").customize(configuration))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("JDBC URL");
        } finally {
            pool.close();
        }
    }

    /**
     * Tables created by the admin after the dynamic role exists are invisible to
     * {@code GRANT ... ON ALL TABLES} issued at role creation. Default privileges cover them,
     * and revocation must drop those privileges or Vault cannot {@code DROP ROLE} on rotation.
     */
    @Test
    void seedScriptGrantsFutureTablesWithoutSchemaCreate() throws Exception {
        String script = Files.readString(Path.of("scripts/vault-seed-database-dev.sh"));

        assertThat(script).contains(
                "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO");
        assertThat(script).contains(
                "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO");
        assertThat(script).contains("revocation_statements=");
        assertThat(script).contains(
                "ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE SELECT, INSERT, UPDATE, DELETE ON TABLES FROM");
        assertThat(script).contains("DROP ROLE IF EXISTS");
        assertThat(script).doesNotContain("CREATE ON SCHEMA");
    }

    private static VaultFlywayMigrationCredentials customizer(final String user, final String password) {
        return new VaultFlywayMigrationCredentials(user, password);
    }

    private static HikariDataSource pool() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("vault-flyway-test");
        dataSource.setJdbcUrl(JDBC_URL);
        dataSource.setUsername("v-dynamic");
        dataSource.setPassword("dynamic-secret");
        dataSource.setMaximumPoolSize(1);
        return dataSource;
    }
}
