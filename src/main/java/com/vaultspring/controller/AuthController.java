package com.vaultspring.controller;

import com.vaultspring.dto.LoginRequest;
import com.vaultspring.dto.LoginResponse;
import com.vaultspring.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints under {@code /api/v1/auth}.
 */
@RestController
@RequestMapping("/api/v1/auth")
public final class AuthController {

    /**
     * Login and token issuance.
     */
    private final AuthService authService;

    /**
     * @param authService authentication service
     */
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    /**
     * @param request validated credentials
     * @return signed JWT access token
     */
    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
