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
```

The local Spring profile connects to MySQL at `localhost:3307`. Email verification and password-reset flows require working SMTP credentials.

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

## API overview

All routes other than authentication and health checks require an access token in the `Authorization: Bearer <access-token>` header. Request and response bodies use JSON.

| Area | Routes |
| --- | --- |
| Health | `GET /api/health` |
| Authentication | `POST /api/auth/register`, `/login`, `/refresh`, `/logout`, `/forget-password`, `/reset-password`, `/verify-email` |
| Tasks | `GET /api/tasks`, `GET /api/tasks/{id}`, `POST /api/tasks`, `PUT /api/tasks/{id}`, `DELETE /api/tasks/{id}` |
| Goals | `GET /api/goals`, `GET /api/goals/mines`, `GET /api/goals/{id}`, `POST /api/goals`, `PUT /api/goals/{id}`, `DELETE /api/goals/{id}` |
| Goal tasks | `GET /api/goals/{goalId}/tasks` |
| Dashboard | `GET /api/dashboard` |

Admin routes include `GET /api/tasks/admin`, `GET /api/tasks/admin/count`, `GET /api/goals/admin`, and `GET /api/dashboard/admin`. Goals and tasks support pagination; tasks can also be filtered by status and priority.

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
