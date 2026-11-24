package io.github.kleilsonsantos.security.vaultspring.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import io.github.kleilsonsantos.security.vaultspring.observability.ObservabilityConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps common failures to RFC 7807 {@link ProblemDetail} responses.
 */
@RestControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Structured application logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * @param ex validation failure from {@code @Valid} request bodies
     * @return 400 with field-level error map
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(final MethodArgumentNotValidException ex) {
        // Spring's getMessage() embeds each FieldError, and FieldError.toString() includes
        // "rejected value [...]". For login and user-create that value is the plaintext
        // password. Log field names only.
        logSafeError("validation.failed", validationFields(ex));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Invalid request");

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * @param ex explicit HTTP status from application services
     * @return problem detail with the same status code
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(final ResponseStatusException ex) {
        if (ex.getStatusCode().is5xxServerError()) {
            logSafeError("http.server_error", ex.getReason());
        }
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getReason());
        problem.setTitle(status.getReasonPhrase());
        return problem;
    }

    /**
     * @param ex validation failure
     * @return field names that failed, without rejected values
     */
    private static String validationFields(final MethodArgumentNotValidException ex) {
        String fields = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .distinct()
                .collect(Collectors.joining(","));
        return fields.isEmpty() ? "n/a" : "fields=" + fields;
    }

    /**
     * @param event safe event name
     * @param detail non-sensitive detail for operators
     */
    private static void logSafeError(final String event, final String detail) {
        LOG.error("event={} correlation_id={} detail={}",
                event,
                MDC.get(ObservabilityConstants.MDC_CORRELATION_ID),
                detail == null ? "n/a" : detail.replace('\n', ' '));
    }
}
