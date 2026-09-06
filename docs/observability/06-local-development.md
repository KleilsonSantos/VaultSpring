# Local development

## Prerequisites

- **Colima** (macOS) or Docker Desktop — VaultSpring scripts assume Colima when present
- VaultSpring running on host port **8080** (`make run-dev`) or app container

### Colima (MacBook)

Colima exposes the daemon at `~/.colima/default/docker.sock`. The Docker CLI may come from Homebrew (`brew install docker`), not Docker Desktop.

```bash
colima status          # must be running
colima start           # if stopped

# Optional — same env the Makefile uses:
source scripts/ensure-colima-docker.sh
docker info
```

If `docker` is not in `PATH`, `scripts/ensure-colima-docker.sh` adds Homebrew's `docker` binary automatically (see also `scripts/run-integration-tests.sh`).

## Start observability stack

```bash
make observability-up
make observability-status
```

Stop:

```bash
make observability-down
```

## Ports

| Service | Host port |
| ------- | --------- |
| Grafana | 3000 |
| Prometheus | 9090 |
| Loki | 3100 |
| Tempo (query) | 3200 |
| OTEL Collector OTLP gRPC | 4317 |
| OTEL Collector OTLP HTTP | 4318 |

Grafana default credentials: `admin` / `admin` (change after first login).

## Prometheus scrape (JWT)

`/actuator/prometheus` requires a Bearer token:

```bash
bash scripts/observability-prometheus-token.sh
docker compose -f docker/observability/docker-compose.observability.yml restart prometheus
```

Target: `host.docker.internal:8080` (Mac/Windows Docker Desktop).

## Traces from local app

Export OTLP to the collector:

```bash
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318/v1/traces
export OTEL_TRACES_SAMPLER_ARG=1.0
make run-dev
```

Generate traffic (login, API calls), then open Grafana → Explore → Tempo.

## Logs

JSON logs appear in the terminal when using `prod` profile. For Loki ingestion in local dev, pipe container stdout or add Promtail later; MVP stack provisions Loki for Grafana correlation wiring.

## Validate configs

```bash
make observability-validate
```
