package com.vaultspring.observability;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Registers common observability tags and helpers.
 */
@Configuration
public class ObservabilityConfig {

    /**
     * Adds low-cardinality application tags to every Micrometer meter.
     *
     * @param environment       deployment environment label
     * @param buildProperties   optional build metadata
     * @return registry customizer
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> observabilityCommonTags(
            @Value("${APP_ENV:dev}") final String environment,
            final Optional<BuildProperties> buildProperties) {
        return registry -> {
            registry.config().commonTags(
                    "application", "vaultspring",
                    "environment", environment);
            buildProperties.ifPresent(properties ->
                    registry.config().commonTags("version", properties.getVersion()));
        };
    }
}
