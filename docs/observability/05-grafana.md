# Grafana

## Provisioning

Dashboards and datasources are versioned under:

```text
docker/observability/grafana/
  provisioning/datasources/datasources.yml
  provisioning/dashboards/dashboards.yml
  dashboards/vaultspring-overview.json
```

## Datasources

| UID | Type | URL |
| --- | ---- | --- |
| `prometheus` | Prometheus | http://prometheus:9090 |
| `loki` | Loki | http://loki:3100 |
| `tempo` | Tempo | http://tempo:3200 |

Tempo ↔ Loki correlation uses `trace_id` derived fields.

## Dashboard: VaultSpring Overview

Panels:

- HTTP request rate and error rate
- HTTP latency p95
- JVM heap used
- Auth login rate by result
- HikariCP active / idle connections

UID: `vaultspring-overview`

## Alerts

Prometheus rules: `docker/observability/prometheus/rules/alerts.yml`

- High HTTP error rate
- High p95 latency
- Scrape target down
- JVM heap pressure
