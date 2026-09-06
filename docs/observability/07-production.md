# Production

## Service identity

| Attribute | Value |
| --------- | ----- |
| `service.name` | `vaultspring` |
| `deployment.environment` | `${APP_ENV}` |
| Version | Spring Boot `build-info` / `info` actuator |

## Recommended settings

```bash
APP_ENV=prod
OTEL_TRACES_SAMPLER_ARG=0.1
OTEL_EXPORTER_OTLP_ENDPOINT=https://otel-collector.internal:4318/v1/traces
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,prometheus,metrics
```

## Logging

Activate `prod` or `prod-vault` profile for JSON Logstash logs to stdout (container-friendly).

## Security

- Keep `/actuator/prometheus` and `/actuator/metrics` authenticated (JWT or network policy).
- `/actuator/health` may remain public for load balancers; use `/actuator/health/liveness` and `/actuator/health/readiness` for orchestrators.
- Never enable debug logging on security or Vault packages in production.

## Health groups

Configured in `application.yml`:

- **Liveness:** `livenessState`
- **Readiness:** `readinessState`, `db`

## Backend coupling

The application exports OTLP and exposes Prometheus metrics only. Grafana, Loki, Tempo, and Prometheus are infrastructure concerns outside the JVM process.
