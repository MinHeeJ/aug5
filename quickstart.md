# KNUE Common Foundation Batch 1 Quickstart

## Run

From the repository root:

```bash
docker compose -f infra/docker-compose.yml up --build
```

The admin UI is available at http://localhost:3000 and the backend health probe is http://localhost:8080/api/health.

Development seed credentials: `admin` / `admin` (R09 system administrator). This credential is for local development only.

## Batch 1 screens

- `/system/users`
- `/system/organizations`
- `/system/positions`
- `/system/roles`
- `/system/user-roles`
- `/system/menu-permissions`
- `/system/feature-permissions`
- `/system/data-scopes`

All browser API requests use relative `/api/...` paths. PostgreSQL data is persisted in the `postgres-data` volume.
