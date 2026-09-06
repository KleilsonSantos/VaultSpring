# Metrics

## Endpoints

| Endpoint | Auth | Purpose |
| -------- | ---- | ------- |
| `/actuator/health` | Public | Liveness / readiness probes |
| `/actuator/prometheus` | JWT | Prometheus scrape |
| `/actuator/metrics` | JWT | Metric discovery |
| `/actuator/info` | JWT | Build info |

## Built-in metrics (do not duplicate)

Spring Boot + Micrometer already expose:

- `http.server.requests` — HTTP rate, latency, status
- `jvm.*` — heap, GC, threads
- `hikaricp.*` — connection pool
- `process.*`, `system.*` — CPU and uptime

## Custom metrics

| Metric | Tags | Description |
| ------ | ---- | ----------- |
| `vaultspring.auth.login.total` | `result=success\|failure` | Login attempts |
| `vaultspring.auth.login.duration` | — | Login latency timer |

Common tags: `application=vaultspring`, `environment=${APP_ENV}`.

## Cardinality rules

Never use as Prometheus labels:

- `trace_id`, `span_id`, `correlation_id`
- JWT, user id, secret paths, exception messages

## Environment variables

| Variable | Default | Purpose |
| -------- | ------- | ------- |
| `APP_ENV` | `dev` | `environment` tag |
| `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` | `health,info,prometheus,metrics` | Exposed actuator endpoints |
