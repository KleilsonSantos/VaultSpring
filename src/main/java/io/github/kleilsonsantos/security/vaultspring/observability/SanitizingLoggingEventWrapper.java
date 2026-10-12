package io.github.kleilsonsantos.security.vaultspring.observability;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;

import java.util.List;
import java.util.Map;

/**
 * Delegates every {@link ILoggingEvent} accessor to a wrapped event, except the message
 * accessors, which are routed through {@link LogSanitizer}. Lets a JSON encoder (e.g.
 * Logstash's) redact secrets the same way the pattern-layout {@code %sanitize} converter
 * does, without depending on encoder-internal provider wiring.
 */
final class SanitizingLoggingEventWrapper implements ILoggingEvent {

    /**
     * Original event, used for every accessor except the message.
     */
    private final ILoggingEvent delegate;

    /**
     * Pre-computed, redacted formatted message.
     */
    private final String sanitizedMessage;

    SanitizingLoggingEventWrapper(final ILoggingEvent delegate) {
        this.delegate = delegate;
        this.sanitizedMessage = LogSanitizer.sanitize(delegate.getFormattedMessage());
    }

    @Override
    public String getThreadName() {
        return delegate.getThreadName();
    }

    @Override
    public Level getLevel() {
        return delegate.getLevel();
    }

    @Override
    public String getMessage() {
        return sanitizedMessage;
    }

    @Override
    public Object[] getArgumentArray() {
        // Logstash's default ArgumentsJsonProvider serializes raw structured arguments
        // (e.g. StructuredArguments/kv()) straight into JSON, bypassing the message-only
        // redaction above entirely. Suppressed rather than passed through so a future log
        // statement can't leak a secret via that path (VS-002); the formatted/sanitized
        // message above already carries the human-readable text of any such call.
        return null;
    }

    @Override
    public String getFormattedMessage() {
        return sanitizedMessage;
    }

    @Override
    public String getLoggerName() {
        return delegate.getLoggerName();
    }

    @Override
    public LoggerContextVO getLoggerContextVO() {
        return delegate.getLoggerContextVO();
    }

    @Override
    public IThrowableProxy getThrowableProxy() {
        return delegate.getThrowableProxy();
    }

    @Override
    public StackTraceElement[] getCallerData() {
        return delegate.getCallerData();
    }

    @Override
    public boolean hasCallerData() {
        return delegate.hasCallerData();
    }

    @Override
    public List<Marker> getMarkerList() {
        return delegate.getMarkerList();
    }

    @Override
    public Map<String, String> getMDCPropertyMap() {
        return delegate.getMDCPropertyMap();
    }

    @Override
    public Map<String, String> getMdc() {
        return delegate.getMdc();
    }

    @Override
    public long getTimeStamp() {
        return delegate.getTimeStamp();
    }

    @Override
    public int getNanoseconds() {
        return delegate.getNanoseconds();
    }

    @Override
    public long getSequenceNumber() {
        return delegate.getSequenceNumber();
    }

    @Override
    public List<KeyValuePair> getKeyValuePairs() {
        return delegate.getKeyValuePairs();
    }

    @Override
    public void prepareForDeferredProcessing() {
        delegate.prepareForDeferredProcessing();
    }
}
