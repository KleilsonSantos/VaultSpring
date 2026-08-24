package com.vaultspring.service;

import com.vaultspring.dto.UserRequest;
import com.vaultspring.dto.UserResponse;
import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * User application service. Hashes passwords with BCrypt so Flyway's
 * {@code CHAR_LENGTH(user_password) >= 60} check is satisfied.
 */
@Service
public class UserService {

    /**
     * Persistence for users.
     */
    private final UserRepository userRepository;

    /**
     * BCrypt encoder (strength 10). Not a full Spring Security filter chain.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * @param userRepository user persistence
     * @param passwordEncoder shared BCrypt encoder
     */
    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return all users without password material
     */
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Creates a user after uniqueness and hashing checks.
     *
     * @param request create payload
     * @return the persisted user
     */
    @Transactional
    public UserResponse create(final UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already registered");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        return toResponse(userRepository.save(user));
    }

    /**
     * @param user persisted entity
     * @return API response
     */
    private UserResponse toResponse(final User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
