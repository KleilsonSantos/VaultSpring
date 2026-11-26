package io.github.kleilsonsantos.security.vaultspring.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Dev seed scripts must stay outside the tree scanned by prod and hom.
 * Flyway scans {@code classpath:db/migration} recursively, so a nested
 * {@code dev/} folder is still applied.
 */
class FlywayDevSeedLocationTest {

    @Test
    void schemaLocationDiscoversOnlySchemaVersions() {
        assertThat(discoveredVersions("classpath:db/migration"))
                .containsExactlyInAnyOrder("1", "4");
    }

    @Test
    void devSeedLocationDiscoversOnlySeedVersions() {
        assertThat(discoveredVersions("classpath:db/dev-seed"))
                .containsExactlyInAnyOrder("2", "3");
    }

    /**
     * @param location Flyway location to scan
     * @return migration versions discovered at that location
     */
    private static Set<String> discoveredVersions(final String location) {
        String databaseName = location.replace(':', '_').replace('/', '_');
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1", "sa", "")
                .locations(location)
                .load();
        return Arrays.stream(flyway.info().all())
                .map(MigrationInfo::getVersion)
                .filter(version -> version != null)
                .map(MigrationVersion::getVersion)
                .collect(Collectors.toSet());
    }
}
