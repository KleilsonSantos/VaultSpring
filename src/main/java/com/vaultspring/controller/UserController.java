package com.vaultspring.controller;

import com.vaultspring.dto.UserRequest;
import com.vaultspring.dto.UserResponse;
import com.vaultspring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HTTP API for users under {@code /api/v1}.
 */
@RestController
@RequestMapping("/api/v1")
public final class UserController {

    /**
     * User application service.
     */
    private final UserService userService;

    /**
     * @param userService user application service
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * @return all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Creates a user.
     *
     * @param request validated payload
     * @return the created user
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody final UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }
}
