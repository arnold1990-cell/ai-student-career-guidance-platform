# Technical Documentation

## 1. Module Overview

### IAM Module
Handles user lifecycle, role mapping, authentication, refresh tokens, and password recovery.

### Student Module
Manages student profiles, qualifications, experience records, and document metadata references.

### Company Module
Supports company onboarding and verification workflows.

### Bursary Module
Manages bursary publishing, approval, search, and student applications.

### Subscription Module
Provides subscription plans, enrollment records, and payment transaction tracking.

### Notification Module
Supports notification persistence and outbox event processing for async delivery.

## 2. API Endpoints Structure
Base path: `/api/v1`

- `/auth/*` IAM authentication and account endpoints
- `/students/*` Student profile and document endpoints
- `/companies/*` Company onboarding and verification endpoints
- `/bursaries/*` Bursary management and applications
- `/subscriptions/*` Subscription lifecycle endpoints
- `/notifications/*` Notification retrieval and status handling
- `/analytics/*` Admin reporting metrics

Swagger: `/swagger-ui.html` (or `/swagger-ui/index.html`).

## 3. Database Schema Details
Migrations location: `backend/src/main/resources/db/migration`

Current migration sequence:
- `V1__init_schema.sql`: initial schema
- `V2__seed_roles.sql`: baseline role data
- `V3__add_notification_read_status.sql`: notification enhancements
- `V4__add_status_to_outbox_events.sql`: fixes missing outbox `status` column

### Outbox Events Fix
`V4__add_status_to_outbox_events.sql` introduces:
- `status VARCHAR(50) NOT NULL DEFAULT 'PENDING'`
- index `idx_outbox_events_status`
- column comment describing valid lifecycle states

## 4. Configuration Guide
Primary config file:
- `backend/src/main/resources/application.yml`

Recommended environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST`
- `SPRING_DATA_REDIS_PORT`
- `JWT_SECRET`

Run profile examples:
```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

## 5. Deployment Checklist
- [ ] Build artifacts generated (`mvn clean package`)
- [ ] All migrations reviewed and applied
- [ ] Database backups verified
- [ ] Secrets configured in deployment environment
- [ ] Health endpoints monitored
- [ ] Swagger/OpenAPI exposure controlled for production
- [ ] Log aggregation configured

## 6. Troubleshooting Guide

### Issue: Schema-validation missing column `status` in `outbox_events`
**Cause**: entity expects `status`, but schema created before this field existed.

**Fix**:
1. Ensure `V4__add_status_to_outbox_events.sql` exists in migration folder.
2. Run `mvn flyway:migrate`.
3. Restart app: `mvn spring-boot:run`.

### Issue: Flyway migration not picked up
- Verify migration naming format `V<version>__<description>.sql`.
- Confirm Flyway points to `classpath:db/migration`.
- Check migration history table (`flyway_schema_history`).

### Issue: Database connection failure
- Validate JDBC URL and credentials.
- Ensure PostgreSQL container/service is reachable.
- Confirm target database exists.

### Issue: Redis connection warnings
- Start Redis service or disable dependent features in local profile.
- Verify `spring.data.redis.*` configuration.
