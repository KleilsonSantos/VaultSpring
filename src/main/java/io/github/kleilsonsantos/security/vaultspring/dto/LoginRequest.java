package io.github.kleilsonsantos.security.vaultspring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Credentials for {@code POST /api/v1/auth/login}.
 *
 * @param email    registered user email
 * @param password plaintext password
 */
public record LoginRequest(
        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
