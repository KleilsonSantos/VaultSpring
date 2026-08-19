package com.vaultspring.controller;

import com.vaultspring.dto.UserRequest;
import com.vaultspring.dto.UserResponse;
import com.vaultspring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Standalone MVC tests for {@link UserController} (no JPA slice).
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    /**
     * User service mock.
     */
    @Mock
    private UserService userService;

    /**
     * Mock MVC.
     */
    private MockMvc mockMvc;

    /**
     * Builds standalone MockMvc with Jackson and Bean Validation.
     */
    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    /**
     * GET /api/v1/users returns the list payload.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void getUsersReturnsOk() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                new UserResponse(1L, "Ada", "ada@example.com", LocalDateTime.now(), LocalDateTime.now())
        ));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ada@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    /**
     * POST /api/v1/users returns 201 when the service creates the user.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void createUserReturnsCreated() throws Exception {
        when(userService.create(any(UserRequest.class))).thenReturn(
                new UserResponse(2L, "Grace", "grace@example.com", LocalDateTime.now(), LocalDateTime.now())
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Grace\",\"email\":\"grace@example.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("grace@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}
