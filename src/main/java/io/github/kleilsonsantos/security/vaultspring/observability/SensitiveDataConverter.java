package io.github.kleilsonsantos.security.vaultspring.observability;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback converter that masks secrets in log messages.
 */
public class SensitiveDataConverter extends ClassicConverter {

    /**
     * {@inheritDoc}
     */
    @Override
    public String convert(final ILoggingEvent event) {
        return LogSanitizer.sanitize(event.getFormattedMessage());
    }
}
