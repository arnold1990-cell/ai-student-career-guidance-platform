# Edutech – AI Powered Education Career Intelligence Platform

## 1) Repository Structure
```text
.
├── backend
│   ├── pom.xml
│   ├── src/main/java/com/edutech/platform
│   │   ├── EdutechApplication.java
│   │   ├── shared
│   │   │   ├── api
│   │   │   ├── audit
│   │   │   ├── config
│   │   │   ├── exception
│   │   │   └── security
│   │   └── modules
│   │       ├── iam
│   │       ├── student
│   │       ├── career
│   │       ├── bursary
│   │       ├── company
│   │       ├── talent
│   │       ├── admin
│   │       ├── subscription
│   │       ├── notification
│   │       └── analytics
│   ├── src/main/resources/application.yml
│   └── src/main/resources/db/migration
├── docker-compose.yml
└── frontend
```

## 2) Tech Stack Decisions (short)
- **Java 21 + Spring Boot 3.3.x**: modern LTS Java and mature enterprise framework.
- **PostgreSQL + Flyway**: strong relational model with deterministic schema migrations.
- **Redis**: cache/rate-limit expansion point.
- **JWT access + refresh strategy**: stateless API auth with secure refresh rotation.
- **Modular Monolith**: strict boundaries now, easy extraction to microservices later.
- **Ports & Adapters**: all external dependencies behind interfaces (storage/search/payments).

## 3) Database design overview
- IAM: users, roles, password reset tokens, login audit.
- Student: profile, qualifications, experiences, document metadata.
- Company + Verification: company profile, verification queue.
- Bursary: bursary, eligibility criteria, applications lifecycle.
- Career: career catalog and explainable recommendations.
- Talent: shortlist, message threads, messages.
- Admin: immutable audit logs, moderation queue, templates.
- Subscriptions + Payments: plan, subscription, transaction.
- Notifications: in-app notification + outbox events.

See `backend/src/main/resources/db/migration/V1__init_schema.sql` and `V2__seed_data.sql`.

## 4) Modules (domain -> application -> infrastructure -> api)
Each module follows:
- `domain`: entities/enums
- `application`: use-cases/services + ports
- `infrastructure`: repositories/adapters
- `api`: controllers + request/response DTOs

## 5) API Endpoints (OpenAPI-style list)
### IAM
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/forgot-password`
- `POST /api/v1/auth/reset-password`

### Student
- `PUT /api/v1/students/profile`
- `GET /api/v1/students/profile`
- `POST /api/v1/students/profile/documents`
- `GET /api/v1/students/profile/documents`

### Company & Verification
- `POST /api/v1/companies/onboard`
- `GET /api/v1/companies/verification-queue` (admin)
- `POST /api/v1/companies/verification-queue/{id}/decision` (admin)

### Bursary
- `POST /api/v1/bursaries`
- `POST /api/v1/bursaries/{id}/approve` (admin)
- `GET /api/v1/bursaries/search`
- `POST /api/v1/bursaries/{id}/apply`

### Subscription + Payment stub
- `POST /api/v1/subscriptions?planCode=...`
- `POST /api/v1/subscriptions/cancel`

### Notifications + outbox
- `GET /api/v1/notifications`
- Outbox worker runs on schedule every 10s.

### Analytics
- `GET /api/v1/analytics/dashboard` (admin)

## 6) Docker Compose local dev
`docker-compose.yml` includes PostgreSQL, Redis, MinIO, backend, frontend.

## 7) Phase 1 MVP implemented
- IAM end-to-end including forgot/reset.
- Student profile CRUD (upsert/read) + document metadata with signed URL stub.
- Company onboarding + verification queue approvals.
- Bursary creation, approval, search, apply.
- Basic analytics dashboard endpoint.
- Subscription + payment gateway stub.
- Notification outbox processing to in-app notifications.

## 8) Phase 2 extraction plan
Already prepared with ports/interfaces:
1. **Notifications service extraction**: move outbox worker + providers behind queue.
2. **AI/Career Intelligence extraction**: recommendation engine and moderation pipeline.
3. **Payment extraction**: replace dummy gateway adapter with PSP adapter.
4. **Search extraction**: swap Postgres adapter for OpenSearch adapter.

## AWS target documentation
- Frontend: S3 + CloudFront.
- Backend: ECS Fargate (preferred) or Elastic Beanstalk (alternative).
- DB: Amazon RDS PostgreSQL.
- Secrets: AWS Secrets Manager.
- Async future: SQS + SNS.
- Observability: CloudWatch + OpenTelemetry collector.

## Run
```bash
docker compose up --build
```

Backend URL: `http://localhost:8080`
Swagger URL: `http://localhost:8080/swagger-ui/index.html`
