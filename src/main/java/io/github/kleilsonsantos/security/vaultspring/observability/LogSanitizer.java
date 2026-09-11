package io.github.kleilsonsantos.security.vaultspring.observability;

import java.util.regex.Pattern;

/**
 * Redacts sensitive substrings before they reach log sinks.
 */
public final class LogSanitizer {

    /**
     * Bearer token prefix pattern.
     */
    private static final Pattern BEARER_TOKEN =
            Pattern.compile("(?i)(Bearer\\s+)[A-Za-z0-9._~+/-]+=*");

    /**
     * Authorization header pattern.
     */
    private static final Pattern AUTHORIZATION_HEADER =
            Pattern.compile("(?i)(Authorization:\\s*)\\S+");

    /**
     * JSON password field pattern.
     */
    private static final Pattern PASSWORD_FIELD =
            Pattern.compile("(?i)(\"password\"\\s*:\\s*\")([^\"]*)(\")");

    /**
     * Vault token assignment pattern.
     */
    private static final Pattern VAULT_TOKEN =
            Pattern.compile("(?i)(vault[_-]?token\\s*[=:]\\s*)\\S+");

    private LogSanitizer() {
    }

    /**
     * @param message raw log message
     * @return sanitized message safe for centralized logging
     */
    public static String sanitize(final String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        String sanitized = message;
        sanitized = BEARER_TOKEN.matcher(sanitized).replaceAll("$1[REDACTED]");
        sanitized = AUTHORIZATION_HEADER.matcher(sanitized).replaceAll("$1[REDACTED]");
        sanitized = PASSWORD_FIELD.matcher(sanitized).replaceAll("$1[REDACTED]$3");
        sanitized = VAULT_TOKEN.matcher(sanitized).replaceAll("$1[REDACTED]");
        return sanitized;
    }
}
