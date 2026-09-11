package io.github.kleilsonsantos.security.vaultspring.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Fail-fast when prod/hom starts with a missing or template JWT secret.
 */
@Component
@Profile({"prod", "hom"})
public class JwtSecretStartupValidator implements InitializingBean {

    /**
     * Bound JWT settings from configuration.
     */
    private final JwtProperties jwtProperties;

    /**
     * @param jwtProperties JWT configuration properties
     */
    public JwtSecretStartupValidator(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Rejects weak or placeholder secrets before serving traffic.
     */
    @Override
    public void afterPropertiesSet() {
        JwtSecretGuard.validateProductionSecret(jwtProperties.getSecret());
    }
}
