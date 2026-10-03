package io.github.kleilsonsantos.security.vaultspring.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Rate limit settings for {@code POST /api/v1/auth/login} (in-memory, per client IP).
 */
@Validated
@ConfigurationProperties(prefix = "vaultspring.security.login-rate-limit")
public class LoginRateLimitProperties {

    /**
     * When false, login requests are not throttled.
     */
    private boolean enabled = true;

    /**
     * Maximum login attempts per client key within {@link #windowSeconds}.
     */
    @Min(1)
    private int maxAttempts = 10;

    /**
     * Fixed window size in seconds.
     */
    @Min(1)
    private int windowSeconds = 60;

    /**
     * @return whether login rate limiting is active
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled whether login rate limiting is active
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return max attempts per window
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }

    /**
     * @param maxAttempts max attempts per window
     */
    public void setMaxAttempts(final int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * @return window length in seconds
     */
    public int getWindowSeconds() {
        return windowSeconds;
    }

    /**
     * @param windowSeconds window length in seconds
     */
    public void setWindowSeconds(final int windowSeconds) {
        this.windowSeconds = windowSeconds;
    }
}
