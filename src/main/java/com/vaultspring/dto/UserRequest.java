package com.vaultspring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload for creating a user.
 *
 * @param name     display name (3–50 characters)
 * @param email    unique email
 * @param password plaintext password; stored as a BCrypt hash
 */
public record UserRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String name,

        @NotBlank
        @Email
        @Size(max = 100)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password
) {
}
