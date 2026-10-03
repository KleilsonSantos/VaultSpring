package io.github.kleilsonsantos.security.vaultspring.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds {@link LoginRateLimitProperties} for login throttling.
 */
@Configuration
@EnableConfigurationProperties(LoginRateLimitProperties.class)
public class LoginRateLimitConfig {
}
