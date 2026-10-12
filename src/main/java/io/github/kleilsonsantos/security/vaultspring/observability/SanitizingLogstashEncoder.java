package io.github.kleilsonsantos.security.vaultspring.observability;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.encoder.LogstashEncoder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link LogstashEncoder} variant that redacts secrets via {@link LogSanitizer} before
 * serialization. The stock encoder's JSON {@code message} field bypasses the pattern-layout
 * {@code %sanitize} converter used by non-JSON profiles (see VS-002 in
 * {@code docs/audit/VAULTSPRING_AUDIT_REPORT.md}), so this wraps each event instead of
 * relying on provider-internal wiring that could change between library versions.
 */
public class SanitizingLogstashEncoder extends LogstashEncoder {

    @Override
    public byte[] encode(final ILoggingEvent event) {
        return super.encode(new SanitizingLoggingEventWrapper(event));
    }

    @Override
    public void encode(final ILoggingEvent event, final OutputStream outputStream) throws IOException {
        super.encode(new SanitizingLoggingEventWrapper(event), outputStream);
    }
}
