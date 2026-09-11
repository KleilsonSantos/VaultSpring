package io.github.kleilsonsantos.security.vaultspring.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link JwtSecretGuard}.
 */
class JwtSecretGuardTest {

    private static final String VALID_SECRET = "custom-production-jwt-secret-at-least-32-chars";

    @Test
    void acceptsStrongCustomSecret() {
        assertThatCode(() -> JwtSecretGuard.validateProductionSecret(VALID_SECRET))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsBlankSecret() {
        assertThatThrownBy(() -> JwtSecretGuard.validateProductionSecret("  "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("VAULTSPRING_JWT_SECRET is required");
    }

    @Test
    void rejectsShortSecret() {
        assertThatThrownBy(() -> JwtSecretGuard.validateProductionSecret("too-short"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 32 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "dev-only-change-me-use-at-least-32-chars",
            "prod-jwt-secret-must-be-set-via-env-var-32c",
            "test-jwt-secret-at-least-32-characters-long"
    })
    void rejectsKnownPlaceholders(final String placeholder) {
        assertThatThrownBy(() -> JwtSecretGuard.validateProductionSecret(placeholder))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("template or dev/test placeholder");
    }
}
