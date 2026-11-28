# DevFlow project context

This is a maintained project snapshot for future coding and learning sessions. Verify it against the current branch and working tree before relying on it.

## Project

DevFlow is a Java 17 / Spring Boot REST API for personal task and goal management. It uses Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation, MySQL 8, JWT, Flyway, and Docker Compose.

## Collaboration workflow

The developer is learning by making application changes personally. Provide implementation guidance and focused examples, but do not edit application code or tests. Keep `.github/copilot-instructions.md`, this context file, and `README.md` accurate as needed.

## Verified capabilities

The current source includes:

- Authentication flows: registration, login, refresh/logout, email verification, and password recovery/reset.
- Task and goal CRUD APIs, task filtering/pagination, goal-task lookup, and dashboard summaries.
- Authenticated user profile read and full-name update (`GET`/`PUT /api/users/me`), mapped to a response DTO that omits the password.
- Role-aware/security-related code and JWT access/refresh tokens.
- Shared static utilities are used for resolving the authenticated user and validating pagination in task/goal services.
- Flyway migrations; the README says schema changes should be made in new migrations rather than by editing applied migrations.
- Maven tests for authentication, tasks, goals, and mail.

Routes and setup details are documented in `README.md`; check the controller and security configuration for the current contract before changing them.

## Latest verification

- `.\mvnw.cmd -q test` passed after the auth response changes: 37 tests across 7 suites, with 0 failures and 0 errors. Docker Engine was available and the Testcontainers-backed MySQL tests ran successfully.
- GitHub Actions was not run separately in this verification.
- The updated Compose configuration validates, but only MySQL was running during the latest check; `http://localhost:8081/api/health` was unreachable because the app service had not been started.
- No build or test has been run since token-version persistence changes began.

## Known gaps

- Forgot-password now returns the controller's same `204` response for known and unknown emails; mail is only sent when the account exists. Login uses the same generic `401` message for unknown email and wrong password; a non-active account also gets `401` but a distinct message.
- Password reset revokes refresh tokens, but already-issued access tokens remain valid until their 24-hour expiry.
- Token-version invalidation is in progress: `User.tokenVersion` and `V3__add_token_version.sql` are present, but the migration has not been verified. `AuthService.resetPassword` increments and saves the version, marks the reset token used, and revokes refresh tokens in the same transaction. `JwtService` and `JwtAuthenticationFilter` use the `tokenVersion` claim; the filter rejects a missing claim and compares numeric values using `Number.longValue()`. Runtime behavior after migration has not yet been verified.
- Current request DTOs constrain names/titles/descriptions to 255 characters, email to 254, passwords to 6-100, UUID reset/verification tokens to 36, and refresh/logout JWTs to 255. Add focused boundary tests later; the developer has deferred writing tests for now.
- Focused tests for authentication and profile flows are absent; current task tests do not cover the task sorting contract. The existing suite passes but does not directly verify the latest auth behavior.
- Docker Compose maps host port `8081` to container port `8080`, but does not activate the `prod` Spring profile, so `application-prod.properties` is not used by that deployment path. Decide which settings the Compose deployment is meant to exercise.
- `GetCurrentUserUtil` assumes an authenticated principal is always a `CustomUserDetails`; calls without an authentication or with another principal type can fail with a null/cast exception.

## Suggested learning roadmap

These are proposals, not committed product requirements. Choose scope with the developer and inspect the latest code before starting:

1. Token invalidation work in progress: verify the V3 migration applies, then confirm matching-version access tokens work and reset-password invalidates old access and refresh tokens.
2. Manually compare login responses for missing email, wrong password, and inactive account; decide whether the inactive-account message is an intentional UX choice.
3. Deferred by the developer: add focused tests for DTO length boundaries, task sorting, and auth/profile flows; verify the suite stays green afterward.
4. Settle whether task/goal updates are full replacement (`PUT`) or partial updates (`PATCH`), and make update behavior consistent with that contract.
5. Decide the intended Spring profile for Docker Compose, then review health checks, logging, and container hardening before treating the published image as production-ready.

For each completed task, record what changed, relevant tests actually run, decisions made, and remaining work here. Keep this concise and never store credentials or personal data in this file.

## Session handoff checklist

At the start of a new session:

1. Read this file and `README.md`.
2. Check the current branch, working tree, and relevant source/tests.
3. Summarize verified project state and ask which next step the developer wants if the task is not already clear.

At the end of a material project change, update this file with verified outcomes and the next agreed step.
