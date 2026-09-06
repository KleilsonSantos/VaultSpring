package com.vaultspring.observability;

/**
 * Shared observability identifiers (headers, MDC keys, metric names).
 */
public final class ObservabilityConstants {

    /**
     * HTTP request/response correlation header.
     */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    /**
     * MDC key for correlation identifier.
     */
    public static final String MDC_CORRELATION_ID = "correlation_id";

    /**
     * Maximum accepted correlation ID length.
     */
    public static final int CORRELATION_ID_MAX_LENGTH = 128;

    /**
     * Micrometer counter for login attempts.
     */
    public static final String METRIC_AUTH_LOGIN_TOTAL = "vaultspring.auth.login.total";

    /**
     * Micrometer timer for login latency.
     */
    public static final String METRIC_AUTH_LOGIN_DURATION = "vaultspring.auth.login.duration";

    private ObservabilityConstants() {
    }
}
