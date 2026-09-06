package com.vaultspring.observability;

import com.vaultspring.entity.User;
import com.vaultspring.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for correlation headers and auth metrics.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorrelationIdIntegrationTest {

    private static final String TEST_EMAIL = "obs-correlation@example.com";

    private static final String TEST_PASSWORD = "secret123";

    /**
     * Web client.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * User repository for seed data.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Password encoder.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Micrometer registry.
     */
    @Autowired
    private MeterRegistry meterRegistry;

    /**
     * Seeds a user for login tests.
     */
    @BeforeEach
    void seedUser() {
        userRepository.findByEmail(TEST_EMAIL).orElseGet(() -> {
            User user = new User();
            user.setName("Obs Test");
            user.setEmail(TEST_EMAIL);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            return userRepository.save(user);
        });
    }

    /**
     * Echoes inbound correlation id in response header.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void returnsProvidedCorrelationId() throws Exception {
        mockMvc.perform(get("/actuator/health")
                        .header(ObservabilityConstants.CORRELATION_ID_HEADER, "trace-req-001"))
                .andExpect(status().isOk())
                .andExpect(header().string(ObservabilityConstants.CORRELATION_ID_HEADER, "trace-req-001"));
    }

    /**
     * Generates correlation id when header is absent.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void generatesCorrelationIdWhenMissing() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists(ObservabilityConstants.CORRELATION_ID_HEADER));
    }

    /**
     * Successful login increments auth success metric.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void loginIncrementsSuccessMetric() throws Exception {
        double before = counterValue("success");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(TEST_EMAIL, TEST_PASSWORD)))
                .andExpect(status().isOk());

        assertThat(counterValue("success")).isGreaterThan(before);
    }

    /**
     * @param result login outcome tag
     * @return counter value for auth login metric
     */
    private double counterValue(final String result) {
        return meterRegistry.get(ObservabilityConstants.METRIC_AUTH_LOGIN_TOTAL)
                .tag("result", result)
                .counter()
                .count();
    }
}
