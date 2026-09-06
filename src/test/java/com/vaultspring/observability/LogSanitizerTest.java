package com.vaultspring.observability;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LogSanitizer}.
 */
class LogSanitizerTest {

    /**
     * Masks bearer tokens in log messages.
     */
    @Test
    void masksBearerTokens() {
        String sanitized = LogSanitizer.sanitize("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.payload");
        assertThat(sanitized).doesNotContain("eyJhbGci");
        assertThat(sanitized).contains("[REDACTED]");
    }

    /**
     * Masks JSON password fields.
     */
    @Test
    void masksPasswordFields() {
        String sanitized = LogSanitizer.sanitize("{\"password\":\"secret123\"}");
        assertThat(sanitized).doesNotContain("secret123");
        assertThat(sanitized).contains("[REDACTED]");
    }

    /**
     * Masks vault token assignments.
     */
    @Test
    void masksVaultTokens() {
        String sanitized = LogSanitizer.sanitize("vault_token=hvs.ABC123");
        assertThat(sanitized).doesNotContain("hvs.ABC123");
    }
}
