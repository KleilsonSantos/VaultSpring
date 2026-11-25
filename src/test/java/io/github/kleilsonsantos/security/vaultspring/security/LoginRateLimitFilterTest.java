package io.github.kleilsonsantos.security.vaultspring.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link LoginRateLimitFilter}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "vaultspring.security.login-rate-limit.enabled=true",
        "vaultspring.security.login-rate-limit.max-attempts=2",
        "vaultspring.security.login-rate-limit.window-seconds=60"
})
class LoginRateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Test
    void returns429WhenLoginRateLimitExceeded() throws Exception {
        String body = """
                {"email":"unknown@example.com","password":"secret123"}\
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(request -> {
                            request.setRemoteAddr("10.2.2.2");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(request -> {
                            request.setRemoteAddr("10.2.2.2");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(request -> {
                            request.setRemoteAddr("10.2.2.2");
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.detail").value("Too many login attempts. Try again later."));
    }

    @Test
    void percentEncodedLoginPathIsRateLimited() throws Exception {
        String body = """
                {"email":"encoded@example.com","password":"secret123"}\
                """;
        HttpClient client = HttpClient.newHttpClient();
        assertThat(postLogin(client, "/api/v1/auth/login", body)).isEqualTo(401);
        assertThat(postLogin(client, "/api/v1/auth/login", body)).isEqualTo(401);
        assertThat(postLogin(client, "/api/v1/auth/%6Cogin", body))
                .as("percent-encoded login path still invokes AuthController")
                .isEqualTo(429);
    }

    private int postLogin(final HttpClient client, final String path, final String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
    }
}
