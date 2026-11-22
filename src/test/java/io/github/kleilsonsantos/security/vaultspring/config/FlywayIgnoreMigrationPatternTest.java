package io.github.kleilsonsantos.security.vaultspring.config;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationState;
import org.flywaydb.core.api.pattern.ValidatePattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prod and hom Flyway ignore patterns must be patterns Flyway 11 can parse at startup.
 */
class FlywayIgnoreMigrationPatternTest {

    /**
     * Flyway 11 rejects a version number as the pattern type, which aborts migrate.
     */
    @Test
    void versionScopedMissingPatternIsRejected() {
        assertThatThrownBy(() -> ValidatePattern.fromPattern("2:missing"))
                .isInstanceOf(FlywayException.class)
                .hasMessageContaining("Invalid migration type");
    }

    /**
     * Removed dev seeds are versioned migrations already applied in some databases.
     * Pending migrations must still be applied.
     */
    @Test
    void versionedMissingIgnoresAppliedAbsentScriptsOnly() {
        ValidatePattern pattern = ValidatePattern.fromPattern("versioned:missing");

        assertThat(pattern.matchesMigration(true, MigrationState.MISSING_SUCCESS)).isTrue();
        assertThat(pattern.matchesMigration(true, MigrationState.PENDING)).isFalse();
        assertThat(pattern.matchesMigration(false, MigrationState.MISSING_SUCCESS)).isFalse();
    }

    /**
     * @param relativePath profile file that configures Flyway for a deployed environment
     * @throws IOException when the profile file cannot be read
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "src/main/resources/application-prod.yml",
            "src/main/resources/application-hom.yml"
    })
    void deployedProfileIgnorePatternsAreAcceptedByFlyway(final String relativePath) throws IOException {
        List<String> patterns = ignorePatterns(Path.of(relativePath));

        assertThat(patterns).contains("*:future", "versioned:missing");
        for (String pattern : patterns) {
            assertThatCode(() -> ValidatePattern.fromPattern(pattern)).doesNotThrowAnyException();
        }
    }

    /**
     * @param path profile YAML path
     * @return {@code spring.flyway.ignore-migration-patterns} entries
     * @throws IOException when the file cannot be read
     */
    @SuppressWarnings("unchecked")
    private static List<String> ignorePatterns(final Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            Map<String, Object> root = new Yaml().load(in);
            Map<String, Object> spring = (Map<String, Object>) root.get("spring");
            Map<String, Object> flyway = (Map<String, Object>) spring.get("flyway");
            Object raw = flyway.get("ignore-migration-patterns");
            assertThat(raw).isInstanceOf(List.class);
            List<String> patterns = new ArrayList<>();
            for (Object item : (List<Object>) raw) {
                patterns.add(String.valueOf(item));
            }
            return patterns;
        }
    }
}
