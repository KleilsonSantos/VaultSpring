package com.vaultspring;

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
     * Flyway seed data is exposed by GET /api/v1/users.
     */
    @Test
    void listUsersReturnsFlywaySeedData() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/users", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("john@example.com");
        assertThat(response.getBody()).doesNotContain("user_password");
    }

    /**
     * POST /api/v1/users persists a user and returns 201.
     */
    @Test
    void createUserReturnsCreated() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"Integration\",\"email\":\"integration@example.com\",\"password\":\"secret123\"}",
                headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/users", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("integration@example.com");
    }

    /**
     * Duplicate emails return RFC 7807 problem details with HTTP 409.
     */
    @Test
    void createUserReturnsConflictForDuplicateEmail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{\"name\":\"John\",\"email\":\"john@example.com\",\"password\":\"secret123\"}",
                headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/users", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("email already registered");
    }
}
