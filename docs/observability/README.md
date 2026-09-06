# Observability

Production-grade observability for VaultSpring: structured logs, Micrometer metrics, OpenTelemetry traces, and an isolated local stack.

## Documents

| Guide | Topic |
| ----- | ----- |
| [01-architecture.md](./01-architecture.md) | End-to-end architecture |
| [02-logging.md](./02-logging.md) | JSON logs, MDC, sanitization |
| [03-metrics.md](./03-metrics.md) | Micrometer / Prometheus |
| [04-tracing.md](./04-tracing.md) | OpenTelemetry / OTLP |
| [05-grafana.md](./05-grafana.md) | Dashboards and datasources |
| [06-local-development.md](./06-local-development.md) | Local stack and ports |
| [07-production.md](./07-production.md) | Production configuration |
| [08-troubleshooting.md](./08-troubleshooting.md) | Common issues |

## Quick start (local)

```bash
make observability-up
make run-dev
bash scripts/observability-prometheus-token.sh   # JWT for Prometheus scrape
```

| Service | URL |
| ------- | --- |
| Grafana | http://localhost:3000 (admin / admin) |
| Prometheus | http://localhost:9090 |
| Tempo (query) | http://localhost:3200 |
| OTEL Collector (OTLP HTTP) | http://localhost:4318 |
| VaultSpring Actuator | http://localhost:8080/actuator/health |

See [06-local-development.md](./06-local-development.md) for full workflow.
