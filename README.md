# Real-Time Chat Service

Java 21, Spring Boot 3.2, stateless JWT authentication, MySQL, Redis.

## Authentication

- Register: `POST /api/auth/register`
  - Body: `{ "username": "...", "email": "...", "password": "..." }`
- Login: `POST /api/auth/login`
  - Body: `{ "usernameOrEmail": "...", "password": "..." }`
  - Returns: `{ "accessToken": "...", "refreshToken": "..." }`
- Protected endpoints require `Authorization: Bearer <accessToken>`.

## Configuration

Environment variables with defaults:

- `DB_URL` (default `jdbc:mysql://localhost:3306/userdb?createDatabaseIfNotExist=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC`)
- `DB_USERNAME` (default `root`)
- `DB_PASSWORD` (default `root`)
- `REDIS_HOST` (default `localhost`)
- `REDIS_PORT` (default `6379`)
- `JWT_SECRET` (default `dev-secret-please-change-in-prod-0123456789012345`)
- `JWT_ACCESS_MINUTES` (default `15`)
- `JWT_REFRESH_DAYS` (default `7`)

## Run Locally

```bash
mvn spring-boot:run
```

## Docker

Build and run:

```bash
docker build -t real-time-chat-service .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/userdb \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root \
  -e JWT_SECRET=change-me-secret-at-least-32-chars \
  real-time-chat-service
```

