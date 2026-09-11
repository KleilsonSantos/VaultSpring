package io.github.kleilsonsantos.security.vaultspring.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT signing and expiry settings. Secret must come from env in non-dev profiles.
 */
@Validated
@ConfigurationProperties(prefix = "vaultspring.jwt")
public class JwtProperties {

    /**
     * HMAC secret (min 32 chars for HS256).
     */
    @NotBlank
    @Size(min = 32, max = 512)
    private String secret;

    /**
     * Access token lifetime in seconds.
     */
    @Min(60)
    private long expirationSeconds = 3600L;

    /**
     * @return HMAC secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     * @param secret HMAC secret
     */
    public void setSecret(final String secret) {
        this.secret = secret;
    }

    /**
     * @return token lifetime in seconds
     */
    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    /**
     * @param expirationSeconds token lifetime in seconds
     */
    public void setExpirationSeconds(final long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }
}
