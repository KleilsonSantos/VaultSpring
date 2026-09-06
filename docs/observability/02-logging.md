# Logging

## Formats

| Profile | Format |
| ------- | ------ |
| `dev`, `test` | Plain text with `correlation_id`, `trace_id`, `span_id` |
| `prod`, `prod-vault`, `hom` | JSON (Logstash encoder) to stdout |

Configuration: `src/main/resources/logback-spring.xml`.

## Fields (JSON profiles)

- `service.name`, `deployment.environment`
- `correlation_id`, `traceId`, `spanId` (from MDC)
- Standard Logstash fields (`@timestamp`, `level`, `logger_name`, `message`)

## Correlation ID

- Header: `X-Correlation-ID`
- Max length: 128 characters
- Allowed charset: `[A-Za-z0-9_-]`
- Invalid or missing values → new UUID
- Filter: `CorrelationIdFilter`

## Sanitization

`LogSanitizer` masks:

- `Bearer` tokens and `Authorization` headers
- JSON `"password"` fields
- `vault_token=` assignments

Never log request bodies for auth or secret operations.

## Environment variables

| Variable | Purpose |
| -------- | ------- |
| `APP_ENV` | `deployment.environment` label (`dev`, `test`, `prod`) |
