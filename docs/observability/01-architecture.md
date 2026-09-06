# Architecture

VaultSpring observability follows a **correlation-first**, vendor-neutral layout:

```text
VaultSpring (Logback JSON + Micrometer + OTEL SDK)
        │ stdout              │ /actuator/prometheus      │ OTLP HTTP
        ▼                     ▼                           ▼
   (host / Docker)       Prometheus                   OTEL Collector
        │                     │                           │
        └──────────── Grafana (datasources) ──────────────┘
                              Tempo (traces)
                              Loki (logs, optional ingest)
```

## Correlation

Every HTTP request receives:

- `trace_id` / `span_id` — Micrometer tracing bridge (OpenTelemetry)
- `correlation_id` — `X-Correlation-ID` header, validated and echoed in the response

These identifiers appear in structured logs when tracing is enabled.

## Security

Observability data must **never** contain secrets, JWTs, Vault tokens, or credentials. See [02-logging.md](./02-logging.md).

## Failure isolation

Export failures (Tempo, Prometheus scrape, Loki) do **not** stop the application. OTLP export uses timeouts and retries configured in Spring Boot and the collector.

## Scope note

VaultSpring uses HashiCorp Vault for **configuration bootstrap** (Spring Cloud Vault), not runtime secret CRUD APIs. Business metrics focus on authentication and HTTP/JVM/DB signals rather than fictional `secret.*` operations.
