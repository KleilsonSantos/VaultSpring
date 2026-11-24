package io.github.kleilsonsantos.security.vaultspring.exception;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.kleilsonsantos.security.vaultspring.controller.AuthController;
import io.github.kleilsonsantos.security.vaultspring.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 */
class GlobalExceptionHandlerTest {

    /**
     * Plaintext password that Spring would otherwise embed in the exception message.
     */
    private static final String PASSWORD = "correct-horse-battery-staple-secret";

    /**
     * Rejected email value that must not be copied into the log line.
     */
    private static final String EMAIL = "not-an-email";

    /**
     * Validation of login credentials logs field names only.
     *
     * <p>{@link MethodArgumentNotValidException#getMessage()} includes
     * {@code rejected value [password]}. Logging that string writes the submitted
     * password to prod JSON logs.</p>
     *
     * @throws Exception when the login method signature cannot be resolved
     */
    @Test
    void validationLogOmitsRejectedPassword() throws Exception {
        Method login = AuthController.class.getMethod("login", LoginRequest.class);
        MethodParameter parameter = new MethodParameter(login, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                new LoginRequest(EMAIL, PASSWORD), "loginRequest");
        bindingResult.addError(new FieldError(
                "loginRequest",
                "password",
                PASSWORD,
                false,
                new String[]{"Size.loginRequest.password"},
                null,
                "size must be between 8 and 72"));
        bindingResult.addError(new FieldError(
                "loginRequest",
                "email",
                EMAIL,
                false,
                new String[]{"Email.loginRequest.email"},
                null,
                "must be a well-formed email address"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
        assertThat(ex.getMessage()).contains(PASSWORD).contains(EMAIL);

        Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        try {
            ProblemDetail problem = new GlobalExceptionHandler().handleValidation(ex);

            assertThat(problem.getStatus()).isEqualTo(400);
            assertThat(problem.getDetail()).doesNotContain(PASSWORD);
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) problem.getProperties().get("errors");
            assertThat(errors).containsEntry("password", "size must be between 8 and 72");
            assertThat(errors.get("password")).doesNotContain(PASSWORD);

            assertThat(appender.list).isNotEmpty();
            String logged = appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .reduce("", (left, right) -> left + "\n" + right);
            assertThat(logged).contains("fields=password,email");
            assertThat(logged).doesNotContain(PASSWORD).doesNotContain(EMAIL);
        } finally {
            logger.detachAppender(appender);
        }
    }
}
