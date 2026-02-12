# AI Student Career Guidance Platform (Phase 0 Security Foundation)

## Stack
- Backend: Java 21, Spring Boot 3.2.8, Spring Security, JPA, PostgreSQL, Flyway, JWT (jjwt 0.12.6)
- Frontend: React + Vite + TypeScript, Axios, React Router
- Infra: Docker Compose (PostgreSQL 16 + Redis 7)

## 1) Start infrastructure
```bash
docker compose up -d
docker compose ps
```

To verify PostgreSQL quickly:
```bash
docker compose exec postgres pg_isready -U postgres -d edutech_db
```

## 2) Run backend
```bash
cd backend
cp .env.example .env
set -a && source .env && set +a
mvn clean install
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`.

## 3) Run frontend
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

## 4) Manual test flow
1. Open `http://localhost:5173/register`, register a `STUDENT` account.
2. Login at `/login`.
3. On dashboard, verify `/api/me` result is shown.
4. Click **Student Ping** => success.
5. Click **Admin Ping** => should return `403` for non-admin users.

## Auth endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout` (stateless stub)

Protected test endpoints:
- `GET /api/me`
- `GET /api/student/ping`
- `GET /api/company/ping`
- `GET /api/admin/ping`
