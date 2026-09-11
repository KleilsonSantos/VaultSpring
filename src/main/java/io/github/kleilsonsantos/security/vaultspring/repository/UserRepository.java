package io.github.kleilsonsantos.security.vaultspring.repository;

import io.github.kleilsonsantos.security.vaultspring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    /**
     * Finds a user by email (login lookup).
     *
     * @param email unique email
     * @return matching user, if any
     */
    Optional<User> findByEmail(String email);
}
