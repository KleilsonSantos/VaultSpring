package com.vaultspring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Functional audit: real HTTP calls against a running embedded server.
 * Run: {@code ./mvnw -B test -Dtest=ApiFunctionalAuditTest}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=true",
        "springdoc.swagger-ui.enabled=true"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiFunctionalAuditTest {

    private static final String AUDIT_EMAIL = "audit-user@example.com";

    private static final String AUDIT_PASSWORD = "secret123";

    private static final PrintStream OUT = System.out;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    private String accessToken;

    private int testNumber;

    @BeforeEach
    void seedUser() {
        baseUrl = "http://localhost:" + port;
        userRepository.findByEmail(AUDIT_EMAIL).orElseGet(() -> {
            User user = new User();
            user.setName("Audit User");
            user.setEmail(AUDIT_EMAIL);
            user.setPassword(passwordEncoder.encode(AUDIT_PASSWORD));
            return userRepository.save(user);
        });
    }

    @Test
    @Order(1)
    void audit01HealthPublic() throws Exception {
        callAndPrint("GET", "/actuator/health", null, null, HttpStatus.OK);
    }

    @Test
    @Order(2)
    void audit02PrometheusWithoutAuth() throws Exception {
        callAndPrint("GET", "/actuator/prometheus", null, null, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(3)
    void audit03InfoWithoutAuth() throws Exception {
        callAndPrint("GET", "/actuator/info", null, null, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(4)
    void audit04LoginValidCredentials() throws Exception {
        String body = "{\"email\":\"" + AUDIT_EMAIL + "\",\"password\":\"" + AUDIT_PASSWORD + "\"}";
        ResponseEntity<String> response = callAndPrint("POST", "/api/v1/auth/login", body, null, HttpStatus.OK);
        JsonNode json = objectMapper.readTree(response.getBody());
        accessToken = json.get("accessToken").asText();
        printField("accessToken (truncated)", truncate(accessToken, 40));
        printField("tokenType", json.get("tokenType").asText());
        printField("expiresIn", json.get("expiresIn").asText());
    }

    @Test
    @Order(5)
    void audit05LoginInvalidPassword() throws Exception {
        String body = "{\"email\":\"" + AUDIT_EMAIL + "\",\"password\":\"wrong-password-99\"}";
        callAndPrint("POST", "/api/v1/auth/login", body, null, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(6)
    void audit06LoginInvalidEmailFormat() throws Exception {
        String body = "{\"email\":\"not-an-email\",\"password\":\"secret123\"}";
        callAndPrint("POST", "/api/v1/auth/login", body, null, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(7)
    void audit07LoginMissingPassword() throws Exception {
        String body = "{\"email\":\"" + AUDIT_EMAIL + "\"}";
        callAndPrint("POST", "/api/v1/auth/login", body, null, HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(8)
    void audit08ListUsersWithoutAuth() throws Exception {
        callAndPrint("GET", "/api/v1/users", null, null, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(9)
    void audit09ListUsersWithBearer() throws Exception {
        ensureToken();
        callAndPrint("GET", "/api/v1/users", null, bearerHeaders(), HttpStatus.OK);
    }

    @Test
    @Order(10)
    void audit10CreateUserWithBearer() throws Exception {
        ensureToken();
        String body = "{\"name\":\"Audit Created\",\"email\":\"audit-created@example.com\","
                + "\"password\":\"secret123\"}";
        callAndPrint("POST", "/api/v1/users", body, bearerHeaders(), HttpStatus.CREATED);
    }

    @Test
    @Order(11)
    void audit11CreateUserDuplicateEmail() throws Exception {
        ensureToken();
        String body = "{\"name\":\"Duplicate\",\"email\":\"" + AUDIT_EMAIL + "\",\"password\":\"secret123\"}";
        callAndPrint("POST", "/api/v1/users", body, bearerHeaders(), HttpStatus.CONFLICT);
    }

    @Test
    @Order(12)
    void audit12CreateUserWithoutAuth() throws Exception {
        String body = "{\"name\":\"No Auth\",\"email\":\"noauth@example.com\",\"password\":\"secret123\"}";
        callAndPrint("POST", "/api/v1/users", body, null, HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(13)
    void audit13CreateUserInvalidPayload() throws Exception {
        ensureToken();
        String body = "{\"name\":\"\",\"email\":\"bad\",\"password\":\"short\"}";
        callAndPrint("POST", "/api/v1/users", body, bearerHeaders(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(14)
    void audit14ListUsersInvalidToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.jwt.token");
        ResponseEntity<String> response = callAndPrint(
                "GET", "/api/v1/users", null, headers, HttpStatus.UNAUTHORIZED);
        JsonNode body = objectMapper.readTree(response.getBody());
        org.assertj.core.api.Assertions.assertThat(body.get("detail").asText())
                .isEqualTo("invalid or expired bearer token");
    }

    @Test
    @Order(15)
    void audit15PrometheusWithBearer() throws Exception {
        ensureToken();
        callAndPrint("GET", "/actuator/prometheus", null, bearerHeaders(), HttpStatus.OK);
    }

    @Test
    @Order(16)
    void audit16SwaggerUiAvailable() throws Exception {
        callAndPrint("GET", "/swagger-ui.html", null, null, HttpStatus.OK);
    }

    @Test
    @Order(17)
    void audit17OpenApiDocsAvailable() throws Exception {
        ResponseEntity<String> response = callAndPrint("GET", "/v3/api-docs", null, null, HttpStatus.OK);
        JsonNode json = objectMapper.readTree(response.getBody());
        printField("openapi version", json.get("openapi").asText());
        printField("paths count", String.valueOf(json.get("paths").size()));

        JsonNode loginSecurity = json.path("paths")
                .path("/api/v1/auth/login")
                .path("post")
                .path("security");
        printField("login security override", loginSecurity.isMissingNode() ? "absent" : loginSecurity.toString());
        org.assertj.core.api.Assertions.assertThat(loginSecurity.isArray()).isTrue();
        org.assertj.core.api.Assertions.assertThat(loginSecurity).isEmpty();
    }

    private void ensureToken() throws Exception {
        if (accessToken != null) {
            return;
        }
        String body = "{\"email\":\"" + AUDIT_EMAIL + "\",\"password\":\"" + AUDIT_PASSWORD + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> login = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/login", new HttpEntity<>(body, headers), String.class);
        accessToken = objectMapper.readTree(login.getBody()).get("accessToken").asText();
    }

    private HttpHeaders bearerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseEntity<String> callAndPrint(
            final String method,
            final String path,
            final String jsonBody,
            final HttpHeaders headers,
            final HttpStatus expected) throws Exception {
        testNumber++;
        String url = baseUrl + path;
        HttpHeaders reqHeaders = headers != null ? headers : new HttpHeaders();
        if (jsonBody != null && !reqHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, reqHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.valueOf(method), entity, String.class);

        boolean passed = response.getStatusCode() == expected;
        OUT.println();
        OUT.println("=".repeat(72));
        OUT.printf("TEST %02d | %s %s%n", testNumber, method, path);
        OUT.println("=".repeat(72));
        OUT.println("URL: " + url);
        if (jsonBody != null) {
            OUT.println("Request body: " + jsonBody);
        }
        if (headers != null && headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            OUT.println("Authorization: Bearer <token-present>");
        }
        OUT.println("Expected status: " + expected.value() + " " + expected.getReasonPhrase());
        OUT.println("Actual status:   " + response.getStatusCode().value() + " "
                + HttpStatus.valueOf(response.getStatusCode().value()).getReasonPhrase());
        OUT.println("Response body:");
        OUT.println(prettyJson(response.getBody()));
        OUT.println("Result: " + (passed ? "PASS" : "FAIL"));
        if (!passed) {
            throw new AssertionError("Expected " + expected + " but got " + response.getStatusCode()
                    + " for " + method + " " + path);
        }
        return response;
    }

    private static String prettyJson(final String raw) throws Exception {
        if (raw == null || raw.isBlank()) {
            return "(empty)";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(raw);
            if (node.isObject() && node.has("accessToken")) {
                Map<String, Object> map = new LinkedHashMap<>();
                node.fields().forEachRemaining(entry -> {
                    if ("accessToken".equals(entry.getKey())) {
                        map.put(entry.getKey(), truncate(entry.getValue().asText(), 40));
                    } else {
                        map.put(entry.getKey(), entry.getValue());
                    }
                });
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
            }
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (Exception ignored) {
            return raw.length() > 500 ? raw.substring(0, 500) + "..." : raw;
        }
    }

    private static void printField(final String name, final String value) {
        OUT.println("  → " + name + ": " + value);
    }

    private static String truncate(final String value, final int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max) + "...[" + value.length() + " chars]";
    }
}
