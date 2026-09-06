package com.vaultspring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end API tests against PostgreSQL with Flyway migrations.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
@Testcontainers(disabledWithoutDocker = true)
class UserApiIT {

    private static final String IT_EMAIL = "it-user@example.com";

    private static final String IT_PASSWORD = "secret123";

    /**
     * PostgreSQL container aligned with Compose ({@code postgres:15}).
     */
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine");

    /**
     * HTTP client bound to the random test port.
     */
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Seeds integration-test users.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Password hashing for seeded users.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Parses login JSON responses.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * JWT bearer token for authenticated requests.
     */
    private String accessToken;

    /**
     * Creates a known IT user and obtains a JWT before each test.
     */
    @BeforeEach
    void authenticate() throws Exception {
        userRepository.findByEmail(IT_EMAIL).orElseGet(() -> {
            User user = new User();
            user.setName("Integration User");
            user.setEmail(IT_EMAIL);
            user.setPassword(passwordEncoder.encode(IT_PASSWORD));
            return userRepository.save(user);
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> loginRequest = new HttpEntity<>(
                "{\"email\":\"" + IT_EMAIL + "\",\"password\":\"" + IT_PASSWORD + "\"}",
                headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/api/v1/auth/login", loginRequest, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode body = objectMapper.readTree(loginResponse.getBody());
        accessToken = body.get("accessToken").asText();
    }

    /**
     * Flyway seed data is exposed by GET /api/v1/users when authenticated.
     */
    @Test
    void listUsersReturnsFlywaySeedData() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/users",
                org.springframework.http.HttpMethod.GET,
                authenticatedEntity(null),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("john@example.com");
        assertThat(response.getBody()).doesNotContain("user_password");
    }

    /**
     * POST /api/v1/users persists a user and returns 201.
     */
    @Test
    void createUserReturnsCreated() {
        HttpEntity<String> request = authenticatedEntity(
                "{\"name\":\"Integration\",\"email\":\"integration@example.com\",\"password\":\"secret123\"}");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/users", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("integration@example.com");
    }

    /**
     * Duplicate emails return RFC 7807 problem details with HTTP 409.
     */
    @Test
    void createUserReturnsConflictForDuplicateEmail() {
        HttpEntity<String> request = authenticatedEntity(
                "{\"name\":\"John\",\"email\":\"john@example.com\",\"password\":\"secret123\"}");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/users", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("email already registered");
    }

    /**
     * Protected routes reject unauthenticated callers.
     */
    @Test
    void listUsersRequiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * @param jsonBody optional JSON body
     * @return HTTP entity with Bearer JWT
     */
    private HttpEntity<String> authenticatedEntity(final String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        if (jsonBody != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity<>(jsonBody, headers);
        }
        return new HttpEntity<>(headers);
    }
}
