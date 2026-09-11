package io.github.kleilsonsantos.security.vaultspring.service;

import io.github.kleilsonsantos.security.vaultspring.dto.LoginRequest;
import io.github.kleilsonsantos.security.vaultspring.dto.LoginResponse;
import io.github.kleilsonsantos.security.vaultspring.entity.User;
import io.github.kleilsonsantos.security.vaultspring.observability.AuthMetrics;
import io.github.kleilsonsantos.security.vaultspring.repository.UserRepository;
import io.github.kleilsonsantos.security.vaultspring.security.JwtService;
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
     * Login observability metrics.
     */
    private final AuthMetrics authMetrics;

    /**
     * @param userRepository  user persistence
     * @param passwordEncoder password hasher
     * @param jwtService      JWT issuer
     * @param authMetrics     login metrics
     */
    public AuthService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final JwtService jwtService,
            final AuthMetrics authMetrics) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authMetrics = authMetrics;
    }

    /**
     * Validates credentials and returns a signed JWT.
     *
     * @param request login payload
     * @return Bearer token response
     */
    @Transactional(readOnly = true)
    public LoginResponse login(final LoginRequest request) {
        return authMetrics.loginDurationTimer().record(() -> {
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> {
                        authMetrics.recordLoginFailure();
                        return invalidCredentials();
                    });

            if (!user.matchesPassword(passwordEncoder, request.password())) {
                authMetrics.recordLoginFailure();
                throw invalidCredentials();
            }

            authMetrics.recordLoginSuccess();
            return jwtService.createLoginResponse(user);
        });
    }

    /**
     * @return generic unauthorized error (no user enumeration)
     */
    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
    }
}
