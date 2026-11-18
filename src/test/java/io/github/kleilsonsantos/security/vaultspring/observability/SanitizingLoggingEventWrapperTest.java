package io.github.kleilsonsantos.security.vaultspring.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SanitizingLoggingEventWrapper}.
 */
class SanitizingLoggingEventWrapperTest {

    /**
     * The raw message is sanitized independently of the formatted one.
     */
    @Test
    void sanitizesRawMessage() {
        ILoggingEvent delegate = mock(ILoggingEvent.class);
        when(delegate.getMessage()).thenReturn("Authorization: Bearer raw-secret-token");
        when(delegate.getFormattedMessage()).thenReturn("Authorization: Bearer raw-secret-token (formatted)");

        SanitizingLoggingEventWrapper wrapper = new SanitizingLoggingEventWrapper(delegate);

        assertThat(wrapper.getMessage())
                .doesNotContain("raw-secret-token")
                .contains("[REDACTED]");
    }

    /**
     * The formatted message is sanitized, and differs from the raw message when the
     * delegate's raw/formatted values differ.
     */
    @Test
    void sanitizesFormattedMessageIndependentlyOfRawMessage() {
        ILoggingEvent delegate = mock(ILoggingEvent.class);
        when(delegate.getMessage()).thenReturn("password={}");
        when(delegate.getFormattedMessage()).thenReturn("{\"password\":\"formatted-secret\"}");

        SanitizingLoggingEventWrapper wrapper = new SanitizingLoggingEventWrapper(delegate);

        assertThat(wrapper.getFormattedMessage())
                .doesNotContain("formatted-secret")
                .contains("[REDACTED]");
        assertThat(wrapper.getFormattedMessage()).isNotEqualTo(wrapper.getMessage());
    }

    /**
     * Structured arguments are suppressed as an empty array, never null, so a caller
     * that iterates the result without a null check does not blow up (VS-002: Logstash's
     * ArgumentsJsonProvider would otherwise serialize raw secrets straight into JSON).
     */
    @Test
    void suppressesArgumentArrayAsEmptyNotNull() {
        ILoggingEvent delegate = mock(ILoggingEvent.class);
        when(delegate.getArgumentArray()).thenReturn(new Object[] {"some-secret-argument"});

        SanitizingLoggingEventWrapper wrapper = new SanitizingLoggingEventWrapper(delegate);

        assertThat(wrapper.getArgumentArray()).isNotNull().isEmpty();
    }

    /**
     * Every other accessor delegates unchanged to the wrapped event.
     */
    @Test
    void delegatesNonMessageAccessors() {
        ILoggingEvent delegate = mock(ILoggingEvent.class);
        when(delegate.getLevel()).thenReturn(Level.WARN);
        when(delegate.getLoggerName()).thenReturn("test-logger");
        when(delegate.getThreadName()).thenReturn("test-thread");

        SanitizingLoggingEventWrapper wrapper = new SanitizingLoggingEventWrapper(delegate);

        assertThat(wrapper.getLevel()).isEqualTo(Level.WARN);
        assertThat(wrapper.getLoggerName()).isEqualTo("test-logger");
        assertThat(wrapper.getThreadName()).isEqualTo("test-thread");
    }
}
