package com.vaultspring.service;

import com.vaultspring.dto.LoginRequest;
import com.vaultspring.dto.LoginResponse;
import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import com.vaultspring.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authenticates users and issues JWT access tokens.
 */
@Service
public class AuthService {

    /**
     * User lookup.
     */
    private final UserRepository userRepository;

    /**
     * BCrypt password verification.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Token issuer.
     */
    private final JwtService jwtService;

    /**
     * @param userRepository  user persistence
     * @param passwordEncoder password hasher
     * @param jwtService      JWT issuer
     */
    public AuthService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Validates credentials and returns a signed JWT.
     *
     * @param request login payload
     * @return Bearer token response
     */
    @Transactional(readOnly = true)
    public LoginResponse login(final LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> invalidCredentials());

        if (!user.matchesPassword(passwordEncoder, request.password())) {
            throw invalidCredentials();
        }

        return jwtService.createLoginResponse(user);
    }

    /**
     * @return generic unauthorized error (no user enumeration)
     */
    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
    }
}
