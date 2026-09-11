package io.github.kleilsonsantos.security.vaultspring.config;

import io.github.kleilsonsantos.security.vaultspring.entity.User;
import io.github.kleilsonsantos.security.vaultspring.entity.UserRole;
import io.github.kleilsonsantos.security.vaultspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web slice tests for {@link SecurityConfig} without PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityFilterChainTest {

    private static final String TEST_EMAIL = "security-test@example.com";

    private static final String TEST_PASSWORD = "secret123";

    private static final String ADMIN_EMAIL = "security-admin@example.com";

    private static final String ADMIN_PASSWORD = "secret123";

    /**
     * Mock MVC with security filters.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Seeds users for login.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Hashes seeded passwords.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Ensures a known user exists for JWT login tests.
     */
    @BeforeEach
    void seedUsers() {
        seedUser(TEST_EMAIL, TEST_PASSWORD, UserRole.USER);
        seedUser(ADMIN_EMAIL, ADMIN_PASSWORD, UserRole.ADMIN);
    }

    private void seedUser(final String email, final String password, final UserRole role) {
        userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName("Security Test");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    /**
     * Liveness/readiness probes stay public.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    /**
     * Prometheus scrape endpoint requires a JWT.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void prometheusRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Bearer token required"));
    }

    /**
     * Authenticated JWT holders can scrape Prometheus.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void prometheusAllowsBearerToken() throws Exception {
        String token = obtainAccessToken();

        mockMvc.perform(get("/actuator/prometheus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * User API requires a valid JWT.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void userApiRequiresJwt() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Bearer token required"));
    }

    /**
     * Login returns a Bearer token for valid credentials.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void loginReturnsJwtForValidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(TEST_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    /**
     * Invalid credentials return 401 without leaking details.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void loginReturnsUnauthorizedForInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"wrong-password-1"}
                                """.formatted(TEST_EMAIL)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Standard users can read their own profile but not list all users.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void standardUserCanAccessMeButNotListAll() throws Exception {
        String token = obtainAccessToken(TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /**
     * Admin users can list all users.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void adminUserCanListAllUsers() throws Exception {
        String token = obtainAccessToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * @return access token from the login endpoint
     * @throws Exception on MockMvc errors
     */
    private String obtainAccessToken() throws Exception {
        return obtainAccessToken(TEST_EMAIL, TEST_PASSWORD);
    }

    /**
     * @param email login email
     * @param password login password
     * @return access token from the login endpoint
     * @throws Exception on MockMvc errors
     */
    private String obtainAccessToken(final String email, final String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        return com.jayway.jsonpath.JsonPath.read(
                result.getResponse().getContentAsString(), "$.accessToken");
    }
}
