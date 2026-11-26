# Dev/IT-only Flyway seed fixtures

`V2__insert_users_table.sql` and `V3__align_seed_passwords.sql` seed demo users
(including a documented `john@example.com` admin account used by `UserApiIT`
and local Swagger/curl testing). They are **not** schema migrations.

This directory is outside `db/migration` on purpose. Flyway scans each location
recursively, so a `dev/` folder nested under `db/migration` is still applied by
`classpath:db/migration` (the prod and hom location). Keep seeds here and add
`classpath:db/dev-seed` only in `application-dev.yml` and
`src/test/resources/application-it.yml`.

Do not add this location to any profile that could point at a real, shared, or
production database.
