package com.vaultspring.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web slice tests for {@link SecurityConfig} without PostgreSQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityFilterChainTest {

    /**
     * Mock MVC with security filters.
     */
    @Autowired
    private MockMvc mockMvc;

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
     * Prometheus scrape endpoint requires authentication.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void prometheusRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Authenticated users can scrape Prometheus.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    @WithMockUser(username = "metrics")
    void prometheusAllowsAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk());
    }

    /**
     * User API stays open until JWT (#6) lands.
     *
     * @throws Exception on MockMvc errors
     */
    @Test
    void userApiIsPublicUntilJwt() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }
}
