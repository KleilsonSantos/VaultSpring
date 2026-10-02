package io.github.kleilsonsantos.security.vaultspring.security;

import io.github.kleilsonsantos.security.vaultspring.observability.AuthMetrics;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Returns HTTP 429 when login rate limit is exceeded for a client IP.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class LoginRateLimitFilter extends OncePerRequestFilter {

    /**
     * Public login endpoint path.
     */
    private static final String LOGIN_PATH = "/api/v1/auth/login";

    /**
     * Rate limit state.
     */
    private final LoginRateLimitService rateLimitService;

    /**
     * Login metrics.
     */
    private final AuthMetrics authMetrics;

    /**
     * @param rateLimitService in-memory limiter
     * @param authMetrics      authentication metrics
     */
    public LoginRateLimitFilter(final LoginRateLimitService rateLimitService, final AuthMetrics authMetrics) {
        this.rateLimitService = rateLimitService;
        this.authMetrics = authMetrics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {
        if (HttpMethod.POST.matches(request.getMethod()) && LOGIN_PATH.equals(request.getRequestURI())) {
            if (!rateLimitService.tryConsume(request)) {
                authMetrics.recordLoginRateLimited();
                ProblemDetailResponseWriter.write(
                        response,
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Too many login attempts. Try again later.",
                        request.getRequestURI());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
