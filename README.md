<div align="center">

<img src="https://img.shields.io/badge/Sprint-1%20%E2%9C%85-6271f5?style=for-the-badge&labelColor=1e1e3a" alt="Sprint 1"/>
<img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge&labelColor=1e1e3a" alt="Status"/>
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge&labelColor=1e1e3a" alt="License"/>

# 🚀 NovaHire

### AI-Powered Interview Practice Platform

**Practice smarter. Get hired faster.**

NovaHire uses Google Gemini AI to generate real interview questions, evaluate your answers,
and give detailed feedback — so you walk into every interview ready to win.

</div>

---

## ✨ Features (Sprint 1 — Foundation)

| Feature | Status |
|---|---|
| User Registration | ✅ Complete |
| JWT Authentication | ✅ Complete |
| Login / Logout | ✅ Complete |
| Protected Routes | ✅ Complete |
| Responsive Dark UI | ✅ Complete |
| PostgreSQL Schema | ✅ Auto-managed |

---

## 🏗️ Architecture

```
novahire/
├── backend/                    # Spring Boot 3.x (Java 17)
│   └── src/main/java/com/novahire/
│       ├── config/             # Security, CORS configuration
│       ├── controller/         # REST API endpoints
│       ├── dto/                # Request/Response DTOs
│       │   ├── request/        # RegisterRequest, LoginRequest
│       │   └── response/       # AuthResponse, UserResponse, ApiResponse<T>
│       ├── entity/             # JPA entities (User)
│       ├── exception/          # GlobalExceptionHandler, custom exceptions
│       ├── repository/         # Spring Data JPA repositories
│       ├── security/           # JWT filter, service, properties
│       └── service/            # Business logic
│
├── frontend/                   # React 18 + Vite + Tailwind CSS
│   └── src/
│       ├── api/                # Axios instance + API modules
│       ├── components/
│       │   ├── auth/           # AuthLayout
│       │   ├── common/         # Logo, LoadingSpinner, ProtectedRoute
│       │   └── layout/         # AppLayout, Sidebar
│       ├── context/            # AuthContext (React Context + localStorage)
│       ├── pages/
│       │   ├── auth/           # LoginPage, RegisterPage
│       │   └── dashboard/      # DashboardPage
│       └── styles/             # Global CSS (Tailwind)
│
├── docker-compose.yml          # PostgreSQL via Docker
└── README.md
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React 18, Vite, Tailwind CSS, React Router v6 |
| **Backend** | Spring Boot 3.2, Java 17, Maven |
| **Database** | PostgreSQL 16 |
| **Auth** | JWT (jjwt 0.11.5), BCrypt (strength 12) |
| **HTTP Client** | Axios with interceptors |
| **Notifications** | react-hot-toast |
| **Icons** | Lucide React |
| **Dev DB** | Docker Compose |

---

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose (for PostgreSQL)
- Maven 3.8+

---

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/novahire.git
cd novahire
```

### 2. Start PostgreSQL

```bash
docker-compose up -d
```

Verify it's running:
```bash
docker-compose ps
# novahire_db   running (healthy)
```

### 3. Start the Backend

```bash
cd backend
./mvnw spring-boot:run
```

Or with Maven installed globally:
```bash
mvn spring-boot:run
```

The backend starts on **http://localhost:8080/api**

> ✅ Spring Boot will auto-create the `users` table via Hibernate `ddl-auto: update`

### 4. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The app opens at **http://localhost:3000**

---

## 🔌 API Reference

### Base URL
```
http://localhost:8080/api
```

### Auth Endpoints

#### Register
```http
POST /auth/register
Content-Type: application/json

{
  "firstName": "Adam",
  "lastName": "Doe",
  "email": "adam@example.com",
  "password": "securepassword"
}
```

**Response 201:**
```json
{
  "success": true,
  "message": "Account created successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "firstName": "Adam",
      "lastName": "Doe",
      "email": "adam@example.com",
      "role": "USER",
      "createdAt": "2024-01-15T10:30:00"
    }
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "adam@example.com",
  "password": "securepassword"
}
```

#### Get Current User (Protected)
```http
GET /users/me
Authorization: Bearer <token>
```

---

## ⚙️ Environment Variables

### Backend (`application.yml`)

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgres` | PostgreSQL password |
| `JWT_SECRET` | (dev secret) | **Change in production!** |

### Frontend (`.env`)

| Variable | Default | Description |
|---|---|---|
| `VITE_API_URL` | `/api` | Backend base URL |

```bash
cp frontend/.env.example frontend/.env
```

---

## 🔐 Security Design

- **Passwords** hashed with BCrypt (cost factor 12)
- **JWT** tokens expire in 24 hours (configurable)
- **CORS** restricted to `localhost:3000` and `localhost:5173`
- **CSRF** disabled (stateless JWT architecture)
- **Input validation** with Jakarta Bean Validation on all DTOs
- **Global exception handler** — never leaks stack traces to clients
- **Stateless sessions** — no server-side session storage

---

## 📊 Database Schema

```sql
CREATE TABLE users (
  id          BIGSERIAL PRIMARY KEY,
  first_name  VARCHAR(50)  NOT NULL,
  last_name   VARCHAR(50)  NOT NULL,
  email       VARCHAR(100) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
  created_at  TIMESTAMP    NOT NULL,
  updated_at  TIMESTAMP    NOT NULL
);
```

> Schema is auto-managed by Hibernate. No manual SQL needed for development.

---

## 🗺️ Project Roadmap

| Sprint | Focus | Status |
|---|---|---|
| **Sprint 1** | Architecture, Auth, JWT, UI Foundation | ✅ **Done** |
| Sprint 2 | Dashboard, User Profile, Interview Setup | 🔜 Pending |
| Sprint 3 | Gemini API, Question Generation | 🔜 Pending |
| Sprint 4 | AI Evaluation, Scoring, Feedback | 🔜 Pending |
| Sprint 5 | History, Statistics, PDF Reports | 🔜 Pending |
| Sprint 6 | UI/UX Polish, Animations, Dark Mode | 🔜 Pending |
| Sprint 7 | Testing, Deployment, Optimization | 🔜 Pending |

---

## 💡 Design Decisions

**Why React Context over Redux?**
Sprint 1 only needs simple auth state. Redux would be over-engineering at this stage. Context scales fine until Sprint 4–5 when we can evaluate if Redux/Zustand is needed.

**Why JWT stored in localStorage?**
Simpler DX for development. For production (Sprint 7), we'll move to HttpOnly cookies to prevent XSS token theft.

**Why `ddl-auto: update` instead of Flyway?**
Migrations (Flyway/Liquibase) add valuable structure but slow down early sprints. We'll introduce Flyway in Sprint 3 when the schema stabilizes.

**Why BCrypt cost 12?**
Industry standard is 10–12. Cost 12 takes ~300ms to hash — slow enough to defeat brute force, fast enough to not impact UX.

---

## 🤝 Contributing

This project is developed Sprint by Sprint with mentor guidance. Each Sprint is a self-contained feature set, reviewed before proceeding.

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

<div align="center">
Built with ❤️ using Spring Boot, React, and Google Gemini AI
</div>
