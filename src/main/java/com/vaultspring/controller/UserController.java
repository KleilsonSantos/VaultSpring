package com.vaultspring.controller;

import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class responsible for handling
 * HTTP requests for the API version 1.
 * Maps all endpoints under the base path "/api/v1".
 */
@RestController
@RequestMapping("/api/v1")
public final class UserController {

    /**
     * Repository used to manage user data.
     */
    private final UserRepository userRepository;

    /**
     * Constructs the controller with the required user repository.
     *
     * @param userRepository the user repository to use
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the list of all users.
     *
     * @return a list of all users wrapped in a ResponseEntity
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Creates a new user.
     * @param user
     * @return the created user wrapped in a ResponseEntity
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userRepository.save(user));
    }
}

