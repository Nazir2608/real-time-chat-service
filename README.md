# Real-Time Chat Service 🚀

A high-performance, production-ready real-time chat backend built with **Java 21**, **Spring Boot 3.4**, and **Stateless JWT Authentication**.

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.4
- **Language**: Java 21
- **Database**: MySQL 8.0 (Persistence)
- **Cache**: Redis (JWT/Session management)
- **Security**: Spring Security + JWT (HS256)
- **Monitoring**: Prometheus + Grafana
- **Containerization**: Docker + Docker Compose

---

## 🔐 Authentication Module

Stateless authentication using Access and Refresh tokens.

### 1. Register User
`POST /api/auth/register`
```json
{
  "username": "user1",
  "email": "user1@example.com",
  "password": "password123"
}
```

### 2. Login
`POST /api/auth/login`
```json
{
  "usernameOrEmail": "user1",
  "password": "password123"
}
```
**Returns**:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbG...",
    "refreshToken": "eyJhbG..."
  },
  "message": "Login successful"
}
```

---

## 💬 Conversation Module (Phase 3)

Manage direct conversations between users.

### 1. Create/Get Direct Conversation
`POST /api/conversations`
- **Auth Required**: Yes
- **Body**:
```json
{
  "targetUserId": "ea0198aa-dd40-4107-a139-7b581c48ddd6"
}
```
*Note: If a conversation already exists between the two users, it returns the existing one.*

### 2. List User Conversations
`GET /api/conversations`
- **Auth Required**: Yes
- **Returns**: A list of all conversations the authenticated user is part of, including the other participant's details.

---

## ⚙️ Configuration

The application can be configured via environment variables (see `docker-compose.yml` for defaults):

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | MySQL Connection URL | `jdbc:mysql://chat-mysql:3306/chatdb` |
| `DB_USERNAME` | MySQL Username | `root` |
| `DB_PASSWORD` | MySQL Password | `root` |
| `REDIS_HOST` | Redis Hostname | `chat-redis` |
| `JWT_SECRET` | 32+ char secret key | `dev-secret-please-change-in-prod-0123456789012345` |

---

## 🐳 Docker Deployment

The easiest way to run the entire stack (App, MySQL, Redis, Monitoring) is using Docker Compose.

### Build and Start
```bash
docker compose up -d --build
```

### View Logs
```bash
docker logs real-time-chat-service -f
```

### Stop Services
```bash
docker compose down
```

---

## 📊 Monitoring

- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3000` (Default login: `admin/admin`)
- **Health Check**: `http://localhost:8080/actuator/health`
