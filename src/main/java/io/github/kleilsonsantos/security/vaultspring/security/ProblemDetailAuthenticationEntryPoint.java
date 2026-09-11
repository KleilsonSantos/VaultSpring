package io.github.kleilsonsantos.security.vaultspring.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns RFC 7807 JSON for unauthenticated requests (401).
 */
@Component
public final class ProblemDetailAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Detail when Authorization header is missing.
     */
    private static final String MISSING_TOKEN_DETAIL = "Bearer token required";

    /**
     * Detail when Bearer token is invalid or expired.
     */
    private static final String INVALID_TOKEN_DETAIL = "invalid or expired bearer token";

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException) throws IOException {
        String detail = authException instanceof InvalidBearerTokenException
                ? INVALID_TOKEN_DETAIL
                : MISSING_TOKEN_DETAIL;
        ProblemDetailResponseWriter.write(
                response, HttpStatus.UNAUTHORIZED, detail, request.getRequestURI());
    }
}
