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
2. (Optional) To self-register `COMPANY` or `ADMIN`, set `COMPANY_INVITATION_CODE` / `ADMIN_INVITATION_CODE` for backend and provide matching invitation code in register form.
3. Login at `/login`.
4. On dashboard, verify `/api/me` result is shown.
5. Click **Student Ping** => success.
6. Click **Admin Ping** => should return `403` for non-admin users.

## Auth endpoints
- `POST /api/auth/register` (`COMPANY`/`ADMIN` registration requires invitation code)
- `POST /api/auth/login`
- `POST /api/auth/logout` (stateless stub)

Protected test endpoints:
- `GET /api/me`
- `GET /api/student/ping`
- `GET /api/company/ping`
- `GET /api/admin/ping`
