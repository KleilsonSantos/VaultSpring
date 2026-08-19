package com.vaultspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main class to bootstrap the VaultSpring application.
 */
@SpringBootApplication
@EnableJpaAuditing
public class VaultSpringApplication {

    /**
     * Entry point of the application.
     *
     * @param args the input arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(VaultSpringApplication.class, args);
    }
}
