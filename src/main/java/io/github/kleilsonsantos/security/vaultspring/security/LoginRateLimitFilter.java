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
import org.springframework.web.util.UrlPathHelper;

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
     * Decodes the path Spring MVC uses for handler matching. {@code getRequestURI()}
     * stays percent-encoded, so {@code /api/v1/auth/%6Cogin} would skip this filter
     * and still invoke login.
     */
    private static final UrlPathHelper PATH_HELPER = new UrlPathHelper();

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
        if (isLoginPost(request)) {
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

    /**
     * @param request incoming request
     * @return true when this POST is the login handler, after path decoding
     */
    private static boolean isLoginPost(final HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        try {
            return LOGIN_PATH.equals(PATH_HELPER.getPathWithinApplication(request));
        } catch (IllegalArgumentException ex) {
            // A malformed escape cannot be the login handler. The container rejects it.
            return false;
        }
    }
}
