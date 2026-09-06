# Troubleshooting

## Docker not found / Colima

Symptoms: `docker: command not found` but `colima status` shows running.

```bash
colima status
source scripts/ensure-colima-docker.sh
docker info
```

The Docker CLI may be installed via Homebrew (`brew install docker`) while the daemon runs in Colima — not Docker Desktop.

## I do not see traces in Grafana

1. Confirm `make observability-up` and OTEL collector is healthy.
2. Verify `OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318/v1/traces` when running on the host.
3. Check sampling: `OTEL_TRACES_SAMPLER_ARG=1.0` for local dev.
4. Generate HTTP traffic after the app starts (traces are created per request).

## Grafana shows no metrics

1. Is VaultSpring running on port 8080?
2. Run `bash scripts/observability-prometheus-token.sh` and restart Prometheus.
3. Open Prometheus UI → Status → Targets — `vaultspring` should be UP.
4. Login JWT must be valid (re-run token script after secret rotation).

## Correlation ID missing

- Request must pass through servlet filter chain (not a static resource).
- Response header `X-Correlation-ID` is always set by `CorrelationIdFilter`.
- Logs include `correlation_id` when using configured logback patterns.

## OTEL Collector unavailable

Expected: application keeps running; spans may be dropped. Check collector logs:

```bash
make observability-logs
```

## Prometheus scrape 401

`/actuator/prometheus` requires JWT. Refresh token file via `scripts/observability-prometheus-token.sh`.

## Observability stack down but app healthy

By design. VaultSpring does not depend on Grafana, Tempo, Loki, or Prometheus at runtime.

## CI validation fails

```bash
bash scripts/validate-observability-config.sh
```

Requires Python 3 with PyYAML and Docker Compose for compose syntax check.
