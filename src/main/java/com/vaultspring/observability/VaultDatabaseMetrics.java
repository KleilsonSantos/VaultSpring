package com.vaultspring.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Metrics for Vault database credential rotation (ADR-0005 Phase 2).
 */
@Component
@ConditionalOnProperty(name = "spring.cloud.vault.database.enabled", havingValue = "true")
public class VaultDatabaseMetrics {

    /**
     * Successful rotation counter.
     */
    private final Counter rotationSuccessCounter;

    /**
     * Failed rotation counter.
     */
    private final Counter rotationFailureCounter;

    /**
     * @param meterRegistry Micrometer registry
     */
    public VaultDatabaseMetrics(final MeterRegistry meterRegistry) {
        this.rotationSuccessCounter = Counter.builder(ObservabilityConstants.METRIC_VAULT_DB_ROTATION_TOTAL)
                .description("Vault database credential rotations")
                .tag("result", "success")
                .register(meterRegistry);
        this.rotationFailureCounter = Counter.builder(ObservabilityConstants.METRIC_VAULT_DB_ROTATION_TOTAL)
                .description("Vault database credential rotations")
                .tag("result", "failure")
                .register(meterRegistry);
    }

    /**
     * Records a successful credential rotation.
     */
    public void recordRotationSuccess() {
        rotationSuccessCounter.increment();
    }

    /**
     * Records a failed credential rotation.
     */
    public void recordRotationFailure() {
        rotationFailureCounter.increment();
    }
}
