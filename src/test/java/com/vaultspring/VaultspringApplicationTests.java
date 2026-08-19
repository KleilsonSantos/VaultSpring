package com.vaultspring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test that the Spring context starts with the {@code test} profile (H2).
 */
@SpringBootTest
@ActiveProfiles("test")
class VaultspringApplicationTests {

    /**
     * Loads the application context.
     */
    @Test
    void contextLoads() {
    }
}
