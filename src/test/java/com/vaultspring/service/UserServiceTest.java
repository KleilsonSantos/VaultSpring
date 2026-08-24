package com.vaultspring.service;

import com.vaultspring.dto.UserRequest;
import com.vaultspring.dto.UserResponse;
import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserService}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * User repository mock.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Password encoder mock.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Service under test.
     */
    private UserService userService;

    /**
     * Wires the service with mocked dependencies.
     */
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    /**
     * findAll maps persisted users to API responses without password material.
     */
    @Test
    void findAllMapsEntitiesToResponses() {
        User user = new User();
        user.setId(1L);
        user.setName("Ada");
        user.setEmail("ada@example.com");
        user.setPassword("hashed");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> responses = userService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).email()).isEqualTo("ada@example.com");
        assertThat(responses.get(0).name()).isEqualTo("Ada");
    }

    /**
     * create hashes the password and persists the user.
     */
    @Test
    void createHashesPasswordAndReturnsResponse() {
        UserRequest request = new UserRequest("Grace", "grace@example.com", "secret123");
        when(userRepository.existsByEmail("grace@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$abcdefghijklmnopqrstuvwxyz012345678901234567890");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2L);
            LocalDateTime now = LocalDateTime.now();
            saved.setCreatedAt(now);
            saved.setUpdatedAt(now);
            return saved;
        });

        UserResponse response = userService.create(request);

        verify(passwordEncoder).encode("secret123");
        verify(userRepository).save(any(User.class));
        assertThat(response.email()).isEqualTo("grace@example.com");
        assertThat(response.name()).isEqualTo("Grace");
    }

    /**
     * create rejects duplicate emails with HTTP 409.
     */
    @Test
    void createThrowsConflictWhenEmailExists() {
        UserRequest request = new UserRequest("Grace", "grace@example.com", "secret123");
        when(userRepository.existsByEmail("grace@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(rse.getReason()).isEqualTo("email already registered");
                });
    }
}
