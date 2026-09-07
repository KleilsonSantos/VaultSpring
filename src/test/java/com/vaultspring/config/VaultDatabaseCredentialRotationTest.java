package com.vaultspring.config;

import com.vaultspring.observability.VaultDatabaseMetrics;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaultDatabaseCredentialRotationTest {

    private static final String BACKEND = "database";

    private static final String ROLE = "vaultspring-app";

    private static final String CREDS_PATH = "database/creds/vaultspring-app";

    @Mock
    private SecretLeaseContainer leaseContainer;

    @Mock
    private VaultDatabaseMetrics metrics;

    @Test
    void credentialsPathBuildsLeasePath() {
        assertThat(VaultDatabaseCredentialRotation.credentialsPath(BACKEND, ROLE))
                .isEqualTo(CREDS_PATH);
    }

    @Test
    void expiredRenewLeaseRequestsRotatingSecret() {
        RequestedSecret source = RequestedSecret.renewable(CREDS_PATH);
        SecretLeaseExpiredEvent event = new SecretLeaseExpiredEvent(source, Lease.none());

        VaultDatabaseCredentialRotation.handleLeaseEvent(
                event, CREDS_PATH, leaseContainer, mock(HikariDataSource.class), new DataSourceProperties(), metrics);

        verify(leaseContainer).requestRotatingSecret(CREDS_PATH);
    }

    @Test
    void rotateLeaseUpdatesHikariCredentials() {
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HikariConfigMXBean configMxBean = mock(HikariConfigMXBean.class);
        HikariPoolMXBean poolMxBean = mock(HikariPoolMXBean.class);
        when(dataSource.getHikariConfigMXBean()).thenReturn(configMxBean);
        when(dataSource.getHikariPoolMXBean()).thenReturn(poolMxBean);

        DataSourceProperties properties = new DataSourceProperties();
        RequestedSecret source = RequestedSecret.rotating(CREDS_PATH);
        SecretLeaseCreatedEvent event = new SecretLeaseCreatedEvent(
                source,
                Lease.none(),
                Map.of("username", "v-user-new", "password", "secret-new"));

        VaultDatabaseCredentialRotation.handleLeaseEvent(
                event, CREDS_PATH, leaseContainer, dataSource, properties, metrics);

        assertThat(properties.getUsername()).isEqualTo("v-user-new");
        assertThat(properties.getPassword()).isEqualTo("secret-new");
        verify(configMxBean).setUsername("v-user-new");
        verify(configMxBean).setPassword("secret-new");
        verify(poolMxBean).softEvictConnections();
        verify(metrics).recordRotationSuccess();
    }

    @Test
    void rotateLeaseWithMissingSecretsRecordsFailure() {
        RequestedSecret source = RequestedSecret.rotating(CREDS_PATH);
        SecretLeaseCreatedEvent event = new SecretLeaseCreatedEvent(
                source,
                Lease.none(),
                Map.of("username", "only-user"));

        VaultDatabaseCredentialRotation.handleLeaseEvent(
                event, CREDS_PATH, leaseContainer, mock(HikariDataSource.class), new DataSourceProperties(), metrics);

        verify(metrics).recordRotationFailure();
    }

    @Test
    void ignoresUnrelatedLeasePaths() {
        RequestedSecret source = RequestedSecret.rotating("database/creds/other-role");
        SecretLeaseExpiredEvent event = new SecretLeaseExpiredEvent(source, Lease.none());

        VaultDatabaseCredentialRotation.handleLeaseEvent(
                event, CREDS_PATH, leaseContainer, mock(HikariDataSource.class), new DataSourceProperties(), metrics);

        verify(leaseContainer, never()).requestRotatingSecret(CREDS_PATH);
    }
}
