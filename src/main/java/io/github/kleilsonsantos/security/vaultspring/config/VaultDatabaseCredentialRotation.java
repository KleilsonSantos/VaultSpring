package io.github.kleilsonsantos.security.vaultspring.config;

import io.github.kleilsonsantos.security.vaultspring.observability.VaultDatabaseMetrics;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * Rotates HikariCP credentials when Vault database lease reaches {@code max_lease_ttl} (ADR-0005 Phase 2).
 */
@Component
@ConditionalOnProperty(name = "spring.cloud.vault.database.enabled", havingValue = "true")
public class VaultDatabaseCredentialRotation {

    /**
     * Logger for rotation events.
     */
    private static final Logger LOG = LoggerFactory.getLogger(VaultDatabaseCredentialRotation.class);

    /**
     * Vault secret key for JDBC username.
     */
    private static final String USERNAME_KEY = "username";

    /**
     * Vault secret key for JDBC password.
     */
    private static final String PASSWORD_KEY = "password";

    /**
     * @param databaseRole     Vault database role
     * @param databaseBackend  Vault database mount (default {@code database})
     * @param leaseContainer   Spring Vault lease container
     * @param dataSource       application JDBC pool
     * @param dataSourceProperties datasource property holder
     * @param metrics          rotation counters
     */
    public VaultDatabaseCredentialRotation(
            @Value("${spring.cloud.vault.database.role}") final String databaseRole,
            @Value("${spring.cloud.vault.database.backend:database}") final String databaseBackend,
            final SecretLeaseContainer leaseContainer,
            final DataSource dataSource,
            final DataSourceProperties dataSourceProperties,
            final VaultDatabaseMetrics metrics) {
        if (!(dataSource instanceof HikariDataSource hikariDataSource)) {
            LOG.warn(
                    "Vault database credential rotation requires HikariCP; found {}",
                    dataSource.getClass().getName());
            return;
        }

        String vaultCredsPath = credentialsPath(databaseBackend, databaseRole);
        leaseContainer.addLeaseListener(event ->
                handleLeaseEvent(event, vaultCredsPath, leaseContainer, hikariDataSource, dataSourceProperties, metrics));
        LOG.info("Registered Vault database credential rotation for path {}", vaultCredsPath);
    }

    /**
     * @param backend Vault database mount
     * @param role    Vault database role
     * @return lease path for dynamic credentials
     */
    static String credentialsPath(final String backend, final String role) {
        return backend + "/creds/" + role;
    }

    static void handleLeaseEvent(
            final SecretLeaseEvent event,
            final String vaultCredsPath,
            final SecretLeaseContainer leaseContainer,
            final HikariDataSource hikariDataSource,
            final DataSourceProperties dataSourceProperties,
            final VaultDatabaseMetrics metrics) {
        if (!vaultCredsPath.equals(event.getSource().getPath())) {
            return;
        }

        RequestedSecret source = event.getSource();
        if (event instanceof SecretLeaseExpiredEvent && source.getMode() == RequestedSecret.Mode.RENEW) {
            LOG.info("Vault database lease expired at max TTL; requesting credential rotation");
            leaseContainer.requestRotatingSecret(vaultCredsPath);
            return;
        }

        if (event instanceof SecretLeaseCreatedEvent createdEvent && source.getMode() == RequestedSecret.Mode.ROTATE) {
            applyRotatedCredentials(createdEvent.getSecrets(), hikariDataSource, dataSourceProperties, metrics);
        }
    }

    private static void applyRotatedCredentials(
            final Map<String, Object> secrets,
            final HikariDataSource hikariDataSource,
            final DataSourceProperties dataSourceProperties,
            final VaultDatabaseMetrics metrics) {
        String username = stringSecret(secrets, USERNAME_KEY);
        String password = stringSecret(secrets, PASSWORD_KEY);
        if (username == null || password == null) {
            LOG.error("Vault database rotation returned incomplete credentials");
            metrics.recordRotationFailure();
            return;
        }

        try {
            dataSourceProperties.setUsername(username);
            dataSourceProperties.setPassword(password);
            hikariDataSource.getHikariConfigMXBean().setUsername(username);
            hikariDataSource.getHikariConfigMXBean().setPassword(password);
            if (hikariDataSource.getHikariPoolMXBean() != null) {
                hikariDataSource.getHikariPoolMXBean().softEvictConnections();
            } else {
                LOG.warn("Hikari pool MXBean unavailable; rotated credentials may not apply to open connections");
            }
            LOG.info("Vault database credentials rotated for user {}", username);
            metrics.recordRotationSuccess();
        } catch (RuntimeException ex) {
            LOG.error("Failed to rotate Vault database credentials", ex);
            metrics.recordRotationFailure();
        }
    }

    private static String stringSecret(final Map<String, Object> secrets, final String key) {
        Object value = secrets.get(key);
        return value == null ? null : Objects.toString(value, null);
    }
}
