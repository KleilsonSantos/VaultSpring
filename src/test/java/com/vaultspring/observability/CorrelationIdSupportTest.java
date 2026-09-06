package com.vaultspring.observability;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CorrelationIdSupport}.
 */
class CorrelationIdSupportTest {

    /**
     * Accepts safe inbound correlation identifiers.
     */
    @Test
    void acceptsValidCorrelationId() {
        assertThat(CorrelationIdSupport.resolve("req-123-abc")).isEqualTo("req-123-abc");
    }

    /**
     * Rejects oversized values.
     */
    @Test
    void rejectsOversizedCorrelationId() {
        String oversized = "a".repeat(ObservabilityConstants.CORRELATION_ID_MAX_LENGTH + 1);
        assertThat(CorrelationIdSupport.resolve(oversized)).matches("^[0-9a-f-]{36}$");
    }

    /**
     * Rejects unsafe characters.
     */
    @Test
    void rejectsUnsafeCharacters() {
        assertThat(CorrelationIdSupport.resolve("bad id with spaces")).matches("^[0-9a-f-]{36}$");
    }

    /**
     * Generates UUID when header is missing.
     */
    @Test
    void generatesWhenMissing() {
        assertThat(CorrelationIdSupport.generate()).matches("^[0-9a-f-]{36}$");
    }
}
