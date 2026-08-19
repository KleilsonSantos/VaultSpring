package com.vaultspring.dto;

import java.time.LocalDateTime;

/**
 * Public user representation. Password hashes are never included.
 *
 * @param id        database identifier
 * @param name      display name
 * @param email     unique email
 * @param createdAt audit timestamp
 * @param updatedAt audit timestamp
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
