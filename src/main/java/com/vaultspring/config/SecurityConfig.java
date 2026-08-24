package com.vaultspring.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * HTTP security for the API and Actuator. JWT login is tracked in issue #6;
 * this chain prepares CSRF, CORS, headers, and endpoint authorization.
 */
@Configuration
public class SecurityConfig {

    /**
     * API CORS paths.
     */
    private static final String API_CORS_PATTERN = "/api/**";

    /**
     * OpenAPI / Swagger UI paths (dev).
     */
    private static final String[] DOCS_PATHS = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    /**
     * @param http security builder
     * @param environment active profiles for HSTS
     * @return configured filter chain
     * @throws Exception on configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http, final Environment environment)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> {
                    headers.contentTypeOptions(Customizer.withDefaults());
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
                    if (isProdProfile(environment)) {
                        headers.httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31_536_000));
                    }
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers(EndpointRequest.to(PrometheusScrapeEndpoint.class)).authenticated()
                        .requestMatchers("/actuator/prometheus").authenticated()
                        .requestMatchers("/actuator/info").authenticated()
                        .requestMatchers(DOCS_PATHS).permitAll()
                        .requestMatchers("/api/v1/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * BCrypt encoder (strength 10). Satisfies Flyway's {@code CHAR_LENGTH(user_password) >= 60} check.
     *
     * @return shared password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @return CORS rules for the public API (JWT #6 will reuse the same origin policy)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(API_CORS_PATTERN, configuration);
        return source;
    }

    /**
     * @param environment Spring environment
     * @return true when prod or prod-vault group is active
     */
    private static boolean isProdProfile(final Environment environment) {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}
