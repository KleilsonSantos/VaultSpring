package io.github.kleilsonsantos.security.vaultspring.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata for springdoc. Endpoints are inferred from controllers at runtime.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Bearer JWT scheme name in OpenAPI components.
     */
    private static final String BEARER_SCHEME = "bearerAuth";

    /**
     * @return API title, version, and Bearer JWT security scheme (dev Swagger UI)
     */
    @Bean
    public OpenAPI vaultSpringOpenApi() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT from POST /api/v1/auth/login");

        return new OpenAPI()
                .info(new Info()
                        .title("VaultSpring API")
                        .version("v1")
                        .description("User management API. Authenticate via POST /api/v1/auth/login, "
                                + "then send Authorization: Bearer <token>."))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME, bearerScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
