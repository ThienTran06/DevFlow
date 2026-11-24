# DevFlow

DevFlow is a Spring Boot REST API for managing personal goals and tasks. It includes JWT-based authentication, email verification and password recovery, task and goal tracking, and dashboard summaries.

## Tech stack

- Java 17 and Spring Boot
- Spring Web MVC, Spring Security, Spring Data JPA, and Bean Validation
- MySQL 8
- JWT access and refresh tokens
- Docker and Docker Compose

## Requirements

- Java 17
- Docker Desktop with Docker Compose
- Docker Hub access to pull `thientran06/devflow-app:latest` (and Docker Hub credentials if the image is private)
- Gmail SMTP credentials for email features

## Configuration

Create a `.env` file in the project root. `.env` is ignored by Git; do not commit real passwords or token secrets.

```dotenv
MYSQL_DATABASE=devflow_db
MYSQL_ROOT_PASSWORD=replace-me
MYSQL_USERNAME=devflow
MYSQL_PASSWORD=replace-me

MYSQL_HOST=localhost
MYSQL_PORT=3307

JWT_ACCESS_SECRET=replace-with-a-long-random-secret
JWT_REFRESH_SECRET=replace-with-a-different-long-random-secret

MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

APP_VERIFY_EMAIL_URL=http://localhost:3000/verify-email
APP_RESET_PASSWORD_URL=http://localhost:3000/reset-password
```

The local Spring profile connects to MySQL at `localhost:3307`. Set the two `APP_*_URL` values to the corresponding pages in your frontend. The frontend receives the token as a `token` query parameter and should submit it to `POST /api/auth/verify-email` or `POST /api/auth/reset-password` in the JSON request body. Email flows require working SMTP credentials.

## Run with Docker Compose

After creating `.env`, pull the image published by the GitHub Actions workflow and start the application and database:

```bash
docker compose pull app
docker compose up -d
```

The API listens on `http://localhost:8080`; MySQL is exposed on port `3307`. Check that the application is running:

```bash
curl http://localhost:8080/api/health
```

To view container logs, run `docker compose logs -f app`. Stop the services with `docker compose down`.

## Run the application locally

Start only the database with Docker Compose, then run Spring Boot using Java 17:

```bash
docker compose up -d mysql
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

## Database migrations

Flyway migrations are stored in `src/main/resources/db/migration` and run when the application starts. Hibernate uses `ddl-auto=validate`, so schema changes must be made through a new migration rather than automatic entity updates.

- A new database runs `V1__create_initial_schema.sql` followed by `V2__add_goal_deadline.sql`.
- The existing local database is baselined at version 1 so Flyway preserves its tables and applies V2.
- Do not edit migrations after they have been applied. Add a new, higher-version migration instead.
- Before baselining an existing production database, back it up and verify that its schema matches V1.

## API overview

All routes other than authentication and health checks require an access token in the `Authorization: Bearer <access-token>` header. Request and response bodies use JSON.

| Area | Routes |
| --- | --- |
| Health | `GET /api/health` |
| Authentication | `POST /api/auth/register`, `/login`, `/refresh`, `/logout`, `/forget-password`, `/reset-password`, `/verify-email` |
| Tasks | `GET /api/tasks`, `GET /api/tasks/{id}`, `POST /api/tasks`, `PUT /api/tasks/{id}`, `DELETE /api/tasks/{id}` |
| Goals | `GET /api/goals` (admin list), `GET /api/goals/mines`, `GET /api/goals/{id}`, `POST /api/goals`, `PUT /api/goals/{id}`, `DELETE /api/goals/{id}` |
| Goal tasks | `GET /api/goals/{goalId}/tasks` |
| Dashboard | `GET /api/dashboard` |
| User profile | `GET /api/users`, `PUT /api/users/me` |

Admin routes include `GET /api/tasks/admin`, `GET /api/tasks/admin/count`, `GET /api/goals` (admin list), `GET /api/goals/admin` (admin count), and `GET /api/dashboard/admin`. `GET /api/goals/count` returns the authenticated user's goal count. Goals and tasks support pagination; tasks can also be filtered by status and priority.

The profile endpoints operate on the authenticated user. Profile updates currently accept `fullName`; responses omit the password.

Task statuses are `TODO`, `IN_PROGRESS`, and `DONE`; priorities are `LOW`, `MEDIUM`, and `HIGH`. Goal statuses are `TODO`, `IN_PROGRESS`, and `DONE`.

### Example requests

Register a user:

```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "Alex Example",
  "email": "alex@example.com",
  "password": "change-this-password"
}
```

Create a goal using the returned access token:

```http
POST /api/goals
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "title": "Learn Spring Boot",
  "description": "Build and ship a REST API",
  "status": "IN_PROGRESS"
}
```

Create a task for an existing goal:

```http
POST /api/tasks
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "title": "Document the API",
  "description": "Write setup and endpoint documentation",
  "status": "TODO",
  "priority": "MEDIUM",
  "deadline": "2026-12-01T17:00:00",
  "goalId": 1
}
```

## CI and Docker image

The GitHub Actions workflow runs the Maven test suite on each push. If it passes, the workflow builds and pushes `thientran06/devflow-app:latest` to Docker Hub. Docker Compose uses this same image tag.
