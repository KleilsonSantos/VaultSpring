package io.github.kleilsonsantos.security.vaultspring.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * Low-cardinality authentication metrics for Prometheus.
 */
@Component
public class AuthMetrics {

    /**
     * Login counter tagged by outcome only.
     */
    private final Counter loginSuccessCounter;

    /**
     * Login counter tagged by outcome only.
     */
    private final Counter loginFailureCounter;

    /**
     * Login latency timer.
     */
    private final Timer loginDurationTimer;

    /**
     * @param meterRegistry Micrometer registry
     */
    public AuthMetrics(final MeterRegistry meterRegistry) {
        this.loginSuccessCounter = Counter.builder(ObservabilityConstants.METRIC_AUTH_LOGIN_TOTAL)
                .description("Total login attempts")
                .tag("result", "success")
                .register(meterRegistry);
        this.loginFailureCounter = Counter.builder(ObservabilityConstants.METRIC_AUTH_LOGIN_TOTAL)
                .description("Total login attempts")
                .tag("result", "failure")
                .register(meterRegistry);
        this.loginDurationTimer = Timer.builder(ObservabilityConstants.METRIC_AUTH_LOGIN_DURATION)
                .description("Login request duration")
                .register(meterRegistry);
    }

    /**
     * Records a successful login.
     */
    public void recordLoginSuccess() {
        loginSuccessCounter.increment();
    }

    /**
     * Records a failed login attempt.
     */
    public void recordLoginFailure() {
        loginFailureCounter.increment();
    }

    /**
     * @return timer for wrapping login service calls
     */
    public Timer loginDurationTimer() {
        return loginDurationTimer;
    }
}
