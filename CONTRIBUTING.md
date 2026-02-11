# Contributing Guide

Thank you for contributing to the AI-Powered Education Career Intelligence Platform.

## 1. Development Principles
- Keep module boundaries clear (`iam`, `student`, `company`, `bursary`, `subscription`, `notification`).
- Prefer small, focused pull requests.
- Maintain backward-compatible API behavior unless explicitly versioned.
- Always ship schema changes via Flyway migrations.

## 2. Code Style Guidelines
- Java 21 and Spring Boot conventions.
- Use meaningful names and single-responsibility classes.
- Keep controller logic thin; business rules belong in services.
- Favor constructor injection.
- Add/update tests for behavior changes.
- Use formatter/linting settings consistently in your IDE.

## 3. Git Workflow
1. Create a branch from `main`:
   - `feature/<scope>-<short-description>`
   - `fix/<scope>-<short-description>`
   - `docs/<scope>-<short-description>`
2. Rebase frequently to keep branch current.
3. Open pull request into `main` when ready.

## 4. Commit Message Convention
Use conventional commits:
- `feat: ...`
- `fix: ...`
- `docs: ...`
- `refactor: ...`
- `test: ...`
- `chore: ...`

Examples:
- `fix(notification): add missing outbox status migration`
- `docs: update deployment checklist`

## 5. Pull Request Process
Each PR should include:
- Problem statement and scope.
- Summary of implementation.
- Migration notes (if schema changed).
- Test evidence (commands and outputs).
- Screenshots for UI changes.

Checklist:
- [ ] Code compiles
- [ ] Tests pass
- [ ] Migrations validated
- [ ] Documentation updated
- [ ] No secrets committed

## 6. Testing Requirements
Before creating/merging a PR, run:
```bash
cd backend
mvn test
```
For migration changes, also validate startup:
```bash
mvn spring-boot:run
```

## 7. Module Development Guide
For any module change:
1. Update `domain` entities/value objects.
2. Implement/adjust `application` service and ports.
3. Adapt `infrastructure` repositories/adapters.
4. Expose via `api` controllers/DTOs.
5. Add migration when persistence changes.
6. Add/adjust tests in `backend/src/test`.

## 8. Security & Secrets
- Never commit credentials or JWT secrets.
- Use environment variables or externalized secret management.
- Mask sensitive data in logs and test fixtures.
