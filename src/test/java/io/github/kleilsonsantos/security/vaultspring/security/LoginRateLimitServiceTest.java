package io.github.kleilsonsantos.security.vaultspring.security;

import io.github.kleilsonsantos.security.vaultspring.config.LoginRateLimitProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link LoginRateLimitService}.
 */
class LoginRateLimitServiceTest {

    private LoginRateLimitProperties properties;

    private LoginRateLimitService service;

    @BeforeEach
    void setUp() {
        properties = new LoginRateLimitProperties();
        properties.setEnabled(true);
        properties.setMaxAttempts(3);
        properties.setWindowSeconds(60);
        service = new LoginRateLimitService(properties);
    }

    @Test
    void resolveClientKeyIgnoresForwardedHeaderByDefault() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 198.51.100.2");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        assertThat(LoginRateLimitService.resolveClientKey(request, properties)).isEqualTo("10.0.0.1");
    }

    /**
     * When trusted, only the last (proxy-observed) hop is ever used as the rate-limit
     * key — an attacker-controlled prefix earlier in the header is always ignored.
     *
     * @param forwardedForHeader raw {@code X-Forwarded-For} header value
     * @param expectedKey        key {@link LoginRateLimitService#resolveClientKey} must return
     */
    @ParameterizedTest(name = "[{index}] X-Forwarded-For=\"{0}\" -> key={1}")
    @MethodSource("trustedLastHopCases")
    void resolveClientKeyUsesLastForwardedForHopWhenTrusted(
            final String forwardedForHeader, final String expectedKey) {
        properties.setTrustForwardedHeader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(forwardedForHeader);

        assertThat(LoginRateLimitService.resolveClientKey(request, properties)).isEqualTo(expectedKey);
    }

    private static Stream<Arguments> trustedLastHopCases() {
        return Stream.of(
                // Multiple hops: the trusted proxy's own appended address (last) wins.
                Arguments.of("203.0.113.1, 198.51.100.2", "198.51.100.2"),
                // Attacker-supplied prefix (e.g. a fake "client" IP) stays in the header, but
                // the trusted proxy appends what it actually observed as the last entry —
                // that's the only one that should be used as the rate-limit key.
                Arguments.of("9.9.9.9, 198.51.100.2", "198.51.100.2"),
                // Single value: last hop and only hop are the same.
                Arguments.of("198.51.100.1", "198.51.100.1"));
    }

    @Test
    void resolveClientKeyUsesRemoteAddressWhenNoForwardedHeader() {
        properties.setTrustForwardedHeader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(LoginRateLimitService.resolveClientKey(request, properties)).isEqualTo("127.0.0.1");
    }

    @Test
    void resolveClientKeyUsesRemoteAddressWhenForwardedHeaderBlank() {
        properties.setTrustForwardedHeader(true);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("   ");
        when(request.getRemoteAddr()).thenReturn("10.0.0.9");

        assertThat(LoginRateLimitService.resolveClientKey(request, properties)).isEqualTo("10.0.0.9");
    }

    @Test
    void allowsUpToMaxAttemptsThenBlocks() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isFalse();
    }

    @Test
    void rotatingForwardedForHeaderDoesNotBypassLimitWhenNotTrusted() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        when(request.getHeader("X-Forwarded-For"))
                .thenReturn("203.0.113.1")
                .thenReturn("203.0.113.2")
                .thenReturn("203.0.113.3")
                .thenReturn("203.0.113.4");

        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isFalse();
    }

    @Test
    void disabledAlwaysAllows() {
        properties.setEnabled(false);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.2");

        for (int i = 0; i < 10; i++) {
            assertThat(service.tryConsume(request)).isTrue();
        }
    }

    @Test
    void resetsCounterWhenWindowRollsOver() throws InterruptedException {
        properties.setMaxAttempts(1);
        properties.setWindowSeconds(1);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.0.0.3");

        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isFalse();

        Thread.sleep(1_100L);

        assertThat(service.tryConsume(request)).isTrue();
    }
}
