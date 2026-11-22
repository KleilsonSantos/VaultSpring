package io.github.kleilsonsantos.security.vaultspring.config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Runs Flyway as the static Postgres admin when Vault supplies the application pool user.
 *
 * <p>The dynamic role ({@code vaultspring-app}) is created without {@code CREATE} on
 * schema {@code public}. PostgreSQL 15+ does not grant that privilege to {@code PUBLIC},
 * so Flyway cannot build {@code flyway_schema_history} or versioned migrations while it
 * borrows the pool's Vault credentials. {@code POSTGRES_USER} is the same admin the
 * Vault database plugin uses. When it is set, migrations keep that login and the pool
 * stays on the short-lived role. An empty value leaves Flyway on the application
 * {@link DataSource} (unit tests and the Vault ITs, which grant their own role).</p>
 */
@Component
@ConditionalOnProperty(name = "spring.cloud.vault.database.enabled", havingValue = "true")
public class VaultFlywayMigrationCredentials implements FlywayConfigurationCustomizer {

    /**
     * Static migration user. Empty when this process should keep Flyway on the pool login.
     */
    private final String migrationUser;

    /**
     * Password for {@link #migrationUser}.
     */
    private final String migrationPassword;

    /**
     * @param migrationUser     {@code POSTGRES_USER}, or blank to skip the override
     * @param migrationPassword {@code POSTGRES_PASSWORD}
     */
    public VaultFlywayMigrationCredentials(
            @Value("${POSTGRES_USER:}") final String migrationUser,
            @Value("${POSTGRES_PASSWORD:}") final String migrationPassword) {
        this.migrationUser = migrationUser;
        this.migrationPassword = migrationPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void customize(final FluentConfiguration configuration) {
        if (!hasText(migrationUser)) {
            return;
        }
        DataSource dataSource = configuration.getDataSource();
        if (dataSource == null) {
            throw new IllegalStateException(
                    "POSTGRES_USER is set but Flyway has no DataSource to copy the JDBC URL from");
        }
        String jdbcUrl = jdbcUrl(dataSource);
        configuration.dataSource(
                jdbcUrl,
                migrationUser,
                migrationPassword == null ? "" : migrationPassword);
    }

    /**
     * @param dataSource application pool, expected to be HikariCP
     * @return JDBC URL without opening a connection
     */
    static String jdbcUrl(final DataSource dataSource) {
        HikariDataSource hikari = hikari(dataSource);
        if (hikari == null || !hasText(hikari.getJdbcUrl())) {
            throw new IllegalStateException(
                    "POSTGRES_USER is set but the application DataSource is not a HikariCP pool with a JDBC URL");
        }
        return hikari.getJdbcUrl();
    }

    private static HikariDataSource hikari(final DataSource dataSource) {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            return hikariDataSource;
        }
        try {
            if (dataSource.isWrapperFor(HikariDataSource.class)) {
                return dataSource.unwrap(HikariDataSource.class);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(
                    "POSTGRES_USER is set but the application DataSource could not be unwrapped to HikariCP",
                    ex);
        }
        return null;
    }

    private static boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }
}
