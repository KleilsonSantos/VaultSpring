package com.vaultspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cryptographic beans. Uses {@code spring-security-crypto} only — not a full Security filter chain.
 */
@Configuration
public class SecurityCryptoConfig {

    /**
     * BCrypt encoder (strength 10). Satisfies Flyway's {@code CHAR_LENGTH(user_password) >= 60} check.
     *
     * @return shared password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
