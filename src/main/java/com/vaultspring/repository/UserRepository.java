package com.vaultspring.repository;

import com.vaultspring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to provide CRUD operations
 * and additional JPA functionalities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a User entity exists with the given email.
     *
     * @param email The email to check for existence.
     * @return true if a User with the given email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
