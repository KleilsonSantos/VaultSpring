package com.vaultspring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata for springdoc. Endpoints are inferred from controllers at runtime.
 */
@Configuration
public class OpenApiConfig {

    /**
     * @return API title and version shown in Swagger UI
     */
    @Bean
    public OpenAPI vaultSpringOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("VaultSpring API")
                        .version("v1")
                        .description("User management API. Passwords are stored as BCrypt hashes."));
    }
}
