package io.github.kleilsonsantos.security.vaultspring.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Context tests for {@link JwtSecretStartupValidator} on prod/hom profiles.
 */
class JwtSecretStartupValidatorTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(JwtSecretStartupValidator.class, JwtConfig.class)
            .withPropertyValues(
                    "vaultspring.jwt.expiration-seconds=3600",
                    "spring.cloud.vault.enabled=false");

    @Test
    void prodProfileFailsWithTemplateSecret() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=prod",
                        "vaultspring.jwt.secret=prod-jwt-secret-must-be-set-via-env-var-32c")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void prodProfileStartsWithStrongSecret() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=prod",
                        "vaultspring.jwt.secret=unique-prod-jwt-secret-generated-for-deploy-32")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void devProfileDoesNotLoadValidator() {
        contextRunner
                .withPropertyValues(
                        "spring.profiles.active=dev",
                        "vaultspring.jwt.secret=dev-only-change-me-use-at-least-32-chars")
                .run(context -> assertThat(context)
                        .hasNotFailed()
                        .doesNotHaveBean(JwtSecretStartupValidator.class));
    }
}
