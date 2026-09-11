package io.github.kleilsonsantos.security.vaultspring.config;

import java.util.Locale;
import java.util.Set;

/**
 * Validates JWT HMAC secrets for non-dev profiles.
 */
public final class JwtSecretGuard {

    /**
     * Minimum length for HS256 secrets (NIST-aligned baseline).
     */
    static final int MIN_LENGTH = 32;

    /**
     * Known template, test, and documentation placeholders that must not ship in prod/hom.
     */
    private static final Set<String> FORBIDDEN_SECRETS = Set.of(
            "dev-only-change-me-use-at-least-32-chars",
            "prod-jwt-secret-must-be-set-via-env-var-32c",
            "test-jwt-secret-at-least-32-characters-long",
            "it-jwt-secret-at-least-32-characters-long!!"
    );

    private JwtSecretGuard() {
    }

    /**
     * @param secret configured JWT signing secret
     * @throws IllegalStateException when the secret is missing, too short, or a known placeholder
     */
    public static void validateProductionSecret(final String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "VAULTSPRING_JWT_SECRET is required for prod/hom profiles (min 32 characters)");
        }
        if (secret.length() < MIN_LENGTH) {
            throw new IllegalStateException(
                    "VAULTSPRING_JWT_SECRET must be at least " + MIN_LENGTH + " characters in prod/hom");
        }
        if (FORBIDDEN_SECRETS.contains(secret.toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException(
                    "VAULTSPRING_JWT_SECRET must not use a template or dev/test placeholder in prod/hom");
        }
    }
}
