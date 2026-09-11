package io.github.kleilsonsantos.security.vaultspring;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies Flyway seed password hash matches the documented dev credential.
 */
class SeedPasswordHashTest {

    /** Dev-only seed password documented in docs/development.md. */
    private static final String SEED_PASSWORD = "secret123";

    /** BCrypt hash from {@code V2__insert_users_table.sql} / {@code V3__align_seed_passwords.sql}. */
    private static final String SEED_BCRYPT_HASH =
            "$2a$10$eUNieSfBKisXr3zK2aaNYe6YgY3DOn0eWuP23yv5xu2ld4XRToQZW";

    @Test
    void flywaySeedPasswordMatchesDocumentedCredential() {
        assertThat(new BCryptPasswordEncoder().matches(SEED_PASSWORD, SEED_BCRYPT_HASH)).isTrue();
    }
}
