package io.github.kleilsonsantos.security.vaultspring.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextVO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression tests for {@link SanitizingLogstashEncoder} (VS-002): a secret-bearing log
 * line must come out redacted in the prod JSON encoder, the same way {@code %sanitize}
 * already redacts it in the pattern-layout (dev/test) profiles.
 */
class SanitizingLogstashEncoderTest {

    /**
     * Real logger context so the encoded event has a valid {@code LoggerContextVO}.
     * Shared across tests (started/stopped once) rather than per-test — Logback's
     * {@code LoggerContext} is heavier than a plain value object and this class only
     * reads from it, never mutates its state.
     */
    private static LoggerContext loggerContext;

    /**
     * Encoder under test.
     */
    private SanitizingLogstashEncoder encoder;

    @BeforeAll
    static void startContext() {
        loggerContext = new LoggerContext();
        loggerContext.start();
    }

    @AfterAll
    static void stopContext() {
        loggerContext.stop();
    }

    @BeforeEach
    void setUp() {
        encoder = new SanitizingLogstashEncoder();
        encoder.setContext(loggerContext);
        encoder.start();
    }

    /**
     * A crafted "Authorization: Bearer ..." log line must not contain the raw token in
     * the encoded JSON output, and must contain the sanitizer's redaction marker instead.
     */
    @Test
    void redactsBearerTokenInEncodedJson() {
        ILoggingEvent event = loggingEvent("Authorization: Bearer super-secret-token-value", null);

        String json = new String(encoder.encode(event), StandardCharsets.UTF_8);

        assertThat(json)
                .doesNotContain("super-secret-token-value")
                .contains("[REDACTED]");
    }

    /**
     * A crafted JSON password field in the message must not leak the raw password.
     */
    @Test
    void redactsPasswordFieldInEncodedJson() {
        ILoggingEvent event = loggingEvent("Login payload: {\"password\":\"super-secret-password\"}", null);

        String json = new String(encoder.encode(event), StandardCharsets.UTF_8);

        assertThat(json)
                .doesNotContain("super-secret-password")
                .contains("[REDACTED]");
    }

    /**
     * Structured arguments (e.g. {@code StructuredArguments.kv()}) must never reach the
     * encoded JSON unredacted, since Logstash's default provider would otherwise
     * serialize them straight into the output, bypassing message-only redaction.
     */
    @Test
    void suppressesStructuredArgumentsInEncodedJson() {
        ILoggingEvent event = loggingEvent("login attempt", new Object[] {"raw-structured-secret-argument"});

        String json = new String(encoder.encode(event), StandardCharsets.UTF_8);

        assertThat(json).doesNotContain("raw-structured-secret-argument");
    }

    /**
     * A message with nothing sensitive in it passes through unredacted, proving the
     * sanitizer only touches matching patterns rather than mangling normal log output.
     */
    @Test
    void leavesNonSensitiveMessageUnchanged() {
        ILoggingEvent event = loggingEvent("user created successfully", null);

        String json = new String(encoder.encode(event), StandardCharsets.UTF_8);

        assertThat(json).contains("user created successfully");
    }

    private ILoggingEvent loggingEvent(final String message, final Object[] argumentArray) {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getMessage()).thenReturn(message);
        when(event.getFormattedMessage()).thenReturn(message);
        when(event.getArgumentArray()).thenReturn(argumentArray);
        when(event.getLevel()).thenReturn(Level.INFO);
        when(event.getLoggerName()).thenReturn(SanitizingLogstashEncoderTest.class.getName());
        when(event.getThreadName()).thenReturn(Thread.currentThread().getName());
        when(event.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(event.getLoggerContextVO()).thenReturn(new LoggerContextVO(loggerContext));
        when(event.getMDCPropertyMap()).thenReturn(Collections.emptyMap());
        when(event.getMdc()).thenReturn(Collections.emptyMap());
        when(event.getMarkerList()).thenReturn(Collections.emptyList());
        when(event.getKeyValuePairs()).thenReturn(Collections.emptyList());
        when(event.getCallerData()).thenReturn(new StackTraceElement[0]);
        return event;
    }
}
