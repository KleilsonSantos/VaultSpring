package io.github.kleilsonsantos.security.vaultspring.observability;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Validates and normalizes inbound correlation identifiers.
 */
public final class CorrelationIdSupport {

    /**
     * Safe correlation token: letters, digits, hyphen, underscore.
     */
    private static final Pattern SAFE_CORRELATION_ID =
            Pattern.compile("^[A-Za-z0-9_-]{1,128}$");

    private CorrelationIdSupport() {
    }

    /**
     * @param candidate inbound header value (may be null or blank)
     * @return validated correlation id or a newly generated UUID
     */
    public static String resolve(final String candidate) {
        if (candidate == null) {
            return generate();
        }
        String trimmed = candidate.trim();
        if (trimmed.isEmpty() || trimmed.length() > ObservabilityConstants.CORRELATION_ID_MAX_LENGTH) {
            return generate();
        }
        if (!SAFE_CORRELATION_ID.matcher(trimmed).matches()) {
            return generate();
        }
        return trimmed;
    }

    /**
     * @return new correlation identifier
     */
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
