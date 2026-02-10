# EduRite — AI-powered Student Career Guidance Platform

## Stack
- Backend: Java 21, Spring Boot 3.2+, Spring MVC, Validation, Security (JWT), JPA, Flyway, MySQL.
- Frontend: React + Vite + TypeScript.
- Infra: Docker Compose (mysql + backend + frontend).

## Quick Start
```bash
docker compose up --build
```

## URLs
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Demo Credentials
> Seeded by Flyway. Default password for demo users: `password`
- Admin: `admin@edurite.com`
- Company: `company@edurite.com`
- Student: `student@edurite.com`

## Profiles
- `dev`: uses sandbox/stub integrations for Email/SMS/Payment/AI/Verification.
- `prod`: placeholder config with TODOs for real providers.

## Stub Integration Testing
- Forgot password: call `POST /api/v1/auth/forgot-password`; reset link logs in backend logs.
- Email/SMS: sent through log-only dev gateways.
- Payment: checkout link and providerRef returned by stub provider.

## File Uploads
- Stored in `/uploads` (docker volume: `uploads_data`).
- MIME + max-size validation + path traversal prevention enabled.

## Postman
Import `postman_collection.json` and set token variables from login responses.

## Notes
- Daily scheduled job archives active bursaries where `endDate < today`.
- AI endpoints are paywalled; BASIC gets 402-style error object for premium endpoints.
