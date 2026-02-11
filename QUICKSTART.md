# Quickstart Guide

## Immediate Fix for Database Error
Error:
`Schema-validation: missing column [status] in table [outbox_events]`

### 1) Confirm migration file
Ensure this file exists:
- `backend/src/main/resources/db/migration/V4__add_status_to_outbox_events.sql`

### 2) Apply migration
```bash
cd backend
mvn flyway:migrate
```

### 3) Start application
```bash
mvn spring-boot:run
```

If successful, the schema validation error should be resolved.

## Full Development Setup

### Prerequisites
- Java 21
- Maven 3.9+
- PostgreSQL 17+
- Redis 7+
- Docker (optional)

### Run infrastructure with Docker
```bash
docker compose up -d postgres redis
```

### Run backend service
```bash
cd backend
mvn clean spring-boot:run
```

## Common Commands
```bash
# Run tests
cd backend && mvn test

# Build backend
cd backend && mvn clean package

# Run only Flyway migrations
cd backend && mvn flyway:migrate

# Show git status
git status
```

## Troubleshooting Tips
- **Migration not running**: ensure file starts with `V4__` and is under `db/migration`.
- **Port conflict**: stop processes using `8080`, `5432`, or `6379`.
- **Bad DB credentials**: update `application.yml` or env vars.
- **Stale schema**: recreate local DB if non-production and rerun migrations.

## Useful Links
- Main documentation: [README.md](README.md)
- Deep technical docs: [DOCUMENTATION.md](DOCUMENTATION.md)
- Contribution standards: [CONTRIBUTING.md](CONTRIBUTING.md)
