# DevFlow project context

This is a maintained project snapshot for future coding and learning sessions. Verify it against the current branch and working tree before relying on it.

## Project

DevFlow is a Java 17 / Spring Boot REST API for personal task and goal management. It uses Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation, MySQL 8, JWT, Flyway, and Docker Compose.

## Verified capabilities

According to the project README and current source layout, the application includes:

- Authentication flows: registration, login, refresh/logout, email verification, and password recovery/reset.
- Task and goal CRUD APIs, task filtering/pagination, goal-task lookup, and dashboard summaries.
- Role-aware/security-related code and JWT access/refresh tokens.
- Flyway migrations; the README says schema changes should be made in new migrations rather than by editing applied migrations.
- Maven tests for authentication, tasks, goals, and mail.

Routes and setup details are documented in `README.md`; check the controller and security configuration for the current contract before changing them.

## Known gap to verify

- At the time this note was created, `AuthController` exposes authentication endpoints but no endpoint for updating a user's profile. Confirm the current user-related controllers and security rules before treating profile update as wholly absent.

## Suggested learning roadmap

These are proposals, not committed product requirements. Choose scope with the developer and inspect the latest code before starting:

1. Add authenticated profile read/update behavior (for example, updating full name), with a request DTO, validation, ownership derived from the authenticated principal, service logic, response contract, and focused tests.
2. Add/verify integration coverage for authorization boundaries and important API error cases.
3. Review API documentation and operational readiness (configuration, health checks, logging, CI/deployment) against the intended users and deployment target.

For each completed task, record what changed, relevant tests actually run, decisions made, and remaining work here. Keep this concise and never store credentials or personal data in this file.

## Session handoff checklist

At the start of a new session:

1. Read this file and `README.md`.
2. Check the current branch, working tree, and relevant source/tests.
3. Summarize verified project state and ask which next step the developer wants if the task is not already clear.

At the end of a material project change, update this file with verified outcomes and the next agreed step.
