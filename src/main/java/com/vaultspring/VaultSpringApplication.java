package com.vaultspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to bootstrap the VaultSpring application.
 */
@SpringBootApplication
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
