package io.github.kleilsonsantos.security.vaultspring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import java.io.IOException;
import java.net.URI;

/**
 * Writes RFC 7807 {@link ProblemDetail} JSON to servlet responses.
 */
public final class ProblemDetailResponseWriter {

    /**
     * Shared JSON mapper for problem responses.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ProblemDetailResponseWriter() {
    }

    /**
     * @param response servlet response
     * @param status   HTTP status
     * @param detail   human-readable detail
     * @param path     request path for {@code instance}
     * @throws IOException on write failures
     */
    public static void write(
            final jakarta.servlet.http.HttpServletResponse response,
            final HttpStatus status,
            final String detail,
            final String path) throws IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(path));

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        OBJECT_MAPPER.writeValue(response.getOutputStream(), problem);
    }
}
