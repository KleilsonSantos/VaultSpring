package com.vaultspring.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns RFC 7807 JSON for forbidden requests (403).
 */
@Component
public final class ProblemDetailAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException accessDeniedException) throws IOException {
        ProblemDetailResponseWriter.write(
                response, HttpStatus.FORBIDDEN, "access denied", request.getRequestURI());
    }
}
