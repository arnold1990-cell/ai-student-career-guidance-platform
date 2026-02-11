# AI-Powered Education Career Intelligence Platform

A modular Spring Boot platform that connects students, companies, and administrators through career intelligence, bursary workflows, subscriptions, notifications, and secure identity management.

## Project Overview
This repository contains a modular monolith designed for rapid product delivery and clear domain boundaries. It supports student career journeys from profile building to bursary applications and company engagement.

## Core Features
- **IAM module**: JWT-based authentication, role-based access control, password reset flow.
- **Student module**: profile management, qualifications, experiences, document metadata.
- **Company module**: onboarding and verification queue workflow.
- **Bursary module**: bursary creation, approval, search, and application lifecycle.
- **Subscription module**: subscription plans, student subscriptions, payment transaction records.
- **Notification module**: in-app notifications and outbox event processing.
- **Analytics module**: dashboard endpoints for platform monitoring.

## Architecture
- **Style**: Modular monolith with clean boundaries.
- **Pattern**: Domain-oriented modules with `domain`, `application`, `infrastructure`, and `api` packages.
- **Persistence**: PostgreSQL with Flyway migrations.
- **Caching/infra hooks**: Redis integration point.
- **Auth**: JWT access/refresh token flow.

## Technology Stack
- Java 21
- Spring Boot 3.2.8
- Maven
- PostgreSQL 17.7
- Flyway
- Redis
- Docker / Docker Compose

## Repository Structure
```text
ai-student-career-guidance-platform/
├── README.md
├── LICENSE
├── CONTRIBUTING.md
├── DOCUMENTATION.md
├── QUICKSTART.md
├── .gitignore
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/edutech/platform/
│       └── resources/
│           ├── application.yml
│           └── db/migration/
└── frontend/ (future expansion)
```

## Installation & Local Development
### 1. Clone
```bash
git clone https://github.com/arnold1990-cell/powered-education-career-intelligence-platform.git
cd powered-education-career-intelligence-platform
```

### 2. Start infrastructure (optional)
```bash
docker compose up -d postgres redis
```

### 3. Run backend
```bash
cd backend
mvn spring-boot:run
```

## Database Schema Fix (Outbox Status)
A new Flyway migration was added:
- `backend/src/main/resources/db/migration/V4__add_status_to_outbox_events.sql`

This migration:
1. Adds `status` column to `outbox_events`.
2. Sets default value to `PENDING`.
3. Creates index `idx_outbox_events_status` for status-based queries.
4. Adds column-level comment documenting status values.

To apply migrations:
```bash
cd backend
mvn flyway:migrate
```

## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html` (or `/swagger-ui/index.html` depending on Springdoc config)
- Postman collection: `postman_collection.json`

## Contributing
Please read [CONTRIBUTING.md](CONTRIBUTING.md) for:
- coding standards
- branching and commit conventions
- PR checklist
- testing expectations

## License
Distributed under the MIT License. See [LICENSE](LICENSE).
