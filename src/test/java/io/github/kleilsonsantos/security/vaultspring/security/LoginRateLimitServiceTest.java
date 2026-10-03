package io.github.kleilsonsantos.security.vaultspring.security;

import io.github.kleilsonsantos.security.vaultspring.config.LoginRateLimitProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void resolveClientKeyUsesFirstForwardedForHop() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 198.51.100.2");

        assertThat(LoginRateLimitService.resolveClientKey(request)).isEqualTo("203.0.113.1");
    }

    @Test
    void resolveClientKeyUsesRemoteAddressWhenNoForwardedHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        assertThat(LoginRateLimitService.resolveClientKey(request)).isEqualTo("127.0.0.1");
    }

    @Test
    void resolveClientKeyUsesRemoteAddressWhenForwardedHeaderBlank() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("   ");
        when(request.getRemoteAddr()).thenReturn("10.0.0.9");

        assertThat(LoginRateLimitService.resolveClientKey(request)).isEqualTo("10.0.0.9");
    }

    @Test
    void resolveClientKeyUsesSingleForwardedForValue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("198.51.100.1");

        assertThat(LoginRateLimitService.resolveClientKey(request)).isEqualTo("198.51.100.1");
    }

    @Test
    void allowsUpToMaxAttemptsThenBlocks() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1");

        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isFalse();
    }

    @Test
    void disabledAlwaysAllows() {
        properties.setEnabled(false);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.2");

        for (int i = 0; i < 10; i++) {
            assertThat(service.tryConsume(request)).isTrue();
        }
    }

    @Test
    void resetsCounterWhenWindowRollsOver() throws InterruptedException {
        properties.setMaxAttempts(1);
        properties.setWindowSeconds(1);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.3");

        assertThat(service.tryConsume(request)).isTrue();
        assertThat(service.tryConsume(request)).isFalse();

        Thread.sleep(1_100L);

        assertThat(service.tryConsume(request)).isTrue();
    }
}
