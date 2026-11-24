# DevFlow project context

This is a maintained project snapshot for future coding and learning sessions. Verify it against the current branch and working tree before relying on it.

## Project

DevFlow is a Java 17 / Spring Boot REST API for personal task and goal management. It uses Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation, MySQL 8, JWT, Flyway, and Docker Compose.

## Verified capabilities

The current source includes:

- Authentication flows: registration, login, refresh/logout, email verification, and password recovery/reset.
- Task and goal CRUD APIs, task filtering/pagination, goal-task lookup, and dashboard summaries.
- Authenticated user profile read (`GET /api/users`) and full-name update (`PUT /api/users/me`), mapped to a response DTO that omits the password.
- Role-aware/security-related code and JWT access/refresh tokens.
- Shared static utilities are used for resolving the authenticated user and validating pagination in task/goal services.
- Flyway migrations; the README says schema changes should be made in new migrations rather than by editing applied migrations.
- Maven tests for authentication, tasks, goals, and mail.

Routes and setup details are documented in `README.md`; check the controller and security configuration for the current contract before changing them.

## Latest verification

- `./mvnw.cmd -q -DskipTests test-compile` succeeded after replacing repeated current-user extraction and paging-helper calls with the shared utilities.
- Tests were not run in this pass, as requested. The last full Maven test run reported 37 tests, 8 failures, and 12 errors.

## Known gaps

- The profile routes are asymmetric: reading uses `/api/users`, while updating uses `/api/users/me`. Consider standardizing both on `/api/users/me` before the API contract is relied on by clients.
- `TaskService.findByGoalId` creates a `PageRequest` without the shared pagination validation. Task sorting also treats every direction other than `asc` as descending rather than rejecting invalid values.
- Access-denied handling uses the project exception type in services, while at least one existing service test expects Spring Security's exception type; choose and use one contract consistently.
- Focused profile tests have not been added yet, and the full Maven test baseline needs diagnosis before it can be considered healthy.

## Suggested learning roadmap

These are proposals, not committed product requirements. Choose scope with the developer and inspect the latest code before starting:

1. Standardize the profile routes and complete profile validation/response behavior.
2. Apply pagination validation consistently and validate task sort direction; standardize access-denied exception handling.
3. Diagnose the existing full-suite failures, then add focused tests for profile and authorization boundaries.
4. Review API documentation and operational readiness (configuration, health checks, logging, CI/deployment) against the intended users and deployment target.

For each completed task, record what changed, relevant tests actually run, decisions made, and remaining work here. Keep this concise and never store credentials or personal data in this file.

## Session handoff checklist

At the start of a new session:

1. Read this file and `README.md`.
2. Check the current branch, working tree, and relevant source/tests.
3. Summarize verified project state and ask which next step the developer wants if the task is not already clear.

At the end of a material project change, update this file with verified outcomes and the next agreed step.
