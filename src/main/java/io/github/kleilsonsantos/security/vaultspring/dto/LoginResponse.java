package io.github.kleilsonsantos.security.vaultspring.dto;

/**
 * JWT login response.
 *
 * @param accessToken signed JWT
 * @param tokenType   always {@code Bearer}
 * @param expiresIn   lifetime in seconds
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
