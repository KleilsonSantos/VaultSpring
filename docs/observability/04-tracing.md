# Tracing

## Stack

- **API / SDK:** OpenTelemetry via `micrometer-tracing-bridge-otel`
- **Export:** OTLP HTTP to collector or backend
- **Propagation:** W3C Trace Context (automatic for HTTP)

## Configuration

`application.yml`:

```yaml
management:
  tracing:
    enabled: true
    sampling:
      probability: ${OTEL_TRACES_SAMPLER_ARG:1.0}
  otlp:
    tracing:
      endpoint: ${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4318/v1/traces}
      timeout: 5s
```

## Environment variables

| Variable | Default | Purpose |
| -------- | ------- | ------- |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4318/v1/traces` | OTLP HTTP endpoint |
| `OTEL_TRACES_SAMPLER_ARG` | `1.0` | Sampling probability (0.0–1.0) |
| `OTEL_SERVICE_NAME` | `vaultspring` (via `spring.application.name`) | Service identity |

## Sampling by environment

| Environment | Suggested `OTEL_TRACES_SAMPLER_ARG` |
| ----------- | ----------------------------------- |
| dev / test | `1.0` |
| prod | `0.1` (adjust via env, not code) |

## Local backend

OTEL Collector (`docker/observability/otel/otel-collector.yml`) forwards traces to Grafana Tempo.

## Fail-safe behavior

If the collector or Tempo is unavailable, the application continues serving traffic. Spans may be dropped after exporter timeouts; adjust `management.otlp.tracing.timeout` if needed.

## Test profile

Tracing export is disabled in `application-test.yml` to keep unit tests hermetic.
