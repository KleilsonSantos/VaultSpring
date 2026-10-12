package io.github.kleilsonsantos.security.vaultspring.security;

import io.github.kleilsonsantos.security.vaultspring.config.LoginRateLimitProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory fixed-window rate limiter keyed by client IP (SEC-002).
 *
 * <p>{@code X-Forwarded-For} is only trusted when
 * {@link LoginRateLimitProperties#isTrustForwardedHeader()} is enabled, since an
 * unauthenticated caller can otherwise set an arbitrary value on every request and
 * obtain a fresh bucket each time, defeating the limiter entirely (VS-003). When trusted,
 * only the <em>last</em> hop is used — a reverse proxy appends the address it observed to
 * the end of the header, so the first hop is still whatever the original client claimed
 * and remains spoofable even behind a real proxy. This assumes exactly one trusted hop
 * (e.g. Render's edge); it is not correct for a chain of several trusted proxies.</p>
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
        String clientKey = resolveClientKey(request, properties);
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
     * @param request    incoming request
     * @param properties rate limit settings (controls whether {@code X-Forwarded-For} is trusted)
     * @return client identifier (last {@code X-Forwarded-For} hop when trusted, otherwise remote address)
     */
    static String resolveClientKey(final HttpServletRequest request, final LoginRateLimitProperties properties) {
        if (properties.isTrustForwardedHeader()) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                int lastComma = forwarded.lastIndexOf(',');
                return (lastComma >= 0 ? forwarded.substring(lastComma + 1) : forwarded).trim();
            }
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
