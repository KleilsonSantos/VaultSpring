# Dev/IT-only Flyway seed fixtures

`V2__insert_users_table.sql` and `V3__align_seed_passwords.sql` seed demo users
(including a documented `john@example.com` / `secret123` admin account used by
`UserApiIT` and local Swagger/curl testing). They are **not** schema migrations.

This directory is only added to `spring.flyway.locations` in `application-dev.yml`
and `src/test/resources/application-it.yml`. `prod` and `hom` intentionally keep
only `classpath:db/migration` (schema-only: `V1`, `V4`) — see
`docs/audit/VAULTSPRING_AUDIT_REPORT.md` finding VS-001.

Do not add this location to any profile that could point at a real, shared, or
production database.
