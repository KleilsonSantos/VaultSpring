package io.github.kleilsonsantos.security.vaultspring.security;

import io.github.kleilsonsantos.security.vaultspring.config.LoginRateLimitProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory fixed-window rate limiter keyed by client IP (SEC-002).
 */
@Service
public class LoginRateLimitService {

    /**
     * Rate limit configuration.
     */
    private final LoginRateLimitProperties properties;

    /**
     * Per-client attempt counters for the current window.
     */
    private final ConcurrentHashMap<String, WindowCounter> windows = new ConcurrentHashMap<>();

    /**
     * @param properties login rate limit settings
     */
    public LoginRateLimitService(final LoginRateLimitProperties properties) {
        this.properties = properties;
    }

    /**
     * @param request HTTP request used to derive the client key
     * @return true when the attempt is allowed under the configured limit
     */
    public boolean tryConsume(final HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }
        String clientKey = resolveClientKey(request);
        long windowStart = currentWindowStart();
        WindowCounter counter = windows.compute(clientKey, (key, existing) -> {
            if (existing == null || existing.windowStart() != windowStart) {
                return new WindowCounter(windowStart, new AtomicInteger(0));
            }
            return existing;
        });
        return counter.attempts().incrementAndGet() <= properties.getMaxAttempts();
    }

    /**
     * @param request incoming request
     * @return client identifier (first {@code X-Forwarded-For} hop or remote address)
     */
    static String resolveClientKey(final HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma >= 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }

    private long currentWindowStart() {
        long now = Instant.now().getEpochSecond();
        int window = properties.getWindowSeconds();
        return now - (now % window);
    }

    private record WindowCounter(long windowStart, AtomicInteger attempts) {
    }
}
