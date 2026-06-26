<div align="center">

<img src="https://img.shields.io/badge/Sprint-2%20%E2%9C%85-6271f5?style=for-the-badge&labelColor=1e1e3a" alt="Sprint 2"/>
<img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge&labelColor=1e1e3a" alt="Status"/>
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge&labelColor=1e1e3a" alt="License"/>

# 🚀 NovaHire

### AI-Powered Interview Practice Platform

**Practice smarter. Get hired faster.**

NovaHire uses Google Gemini AI to generate real interview questions, evaluate your answers,
and give detailed feedback — so you walk into every interview ready to win.

</div>

---

## ✨ Features

### Sprint 1 — Foundation

| Feature | Status |
|---|---|
| User Registration | ✅ Complete |
| JWT Authentication | ✅ Complete |
| Login / Logout | ✅ Complete |
| Protected Routes | ✅ Complete |
| Responsive Dark UI | ✅ Complete |
| PostgreSQL Schema | ✅ Auto-managed |

### Sprint 2 — Profile & Interview Setup

| Feature | Status |
|---|---|
| Edit profile (name, email, target role, experience, language, country) | ✅ Complete |
| Change password | ✅ Complete |
| Avatar upload (Base64, stored in DB) | ✅ Complete |
| CV upload (client-side text extraction) | ✅ Complete |
| Interview creation wizard (role, tech stack, duration, language) | ✅ Complete |
| Interview history & lookup endpoints | ✅ Complete |
| Dashboard statistics endpoint (totals, scores, recent sessions) | ✅ Complete |

---

## 🏗️ Architecture

```
novahire/
├── backend/                    # Spring Boot 3.x (Java 17)
│   └── src/main/java/com/novahire/
│       ├── config/             # Security, CORS configuration
│       ├── controller/         # AuthController, UserController, InterviewController
│       ├── dto/
│       │   ├── request/        # RegisterRequest, LoginRequest, UpdateProfileRequest,
│       │   │                   #   ChangePasswordRequest, UpdateAvatarRequest, UpdateCvRequest,
│       │   │                   #   CreateInterviewRequest
│       │   └── response/       # AuthResponse, UserResponse, ApiResponse<T>,
│       │                       #   InterviewResponse, DashboardStatsResponse
│       ├── entity/             # User, Interview
│       ├── exception/          # GlobalExceptionHandler + custom exceptions
│       │                       #   (EmailAlreadyExists, BadRequest, Forbidden, ResourceNotFound)
│       ├── repository/         # UserRepository, InterviewRepository
│       ├── security/           # JWT filter, service, properties
│       └── service/            # AuthService, UserService, InterviewService
│
├── frontend/                   # React 18 + Vite + Tailwind CSS
│   └── src/
│       ├── api/                # Axios instance + API modules (auth, users, interviews)
│       ├── components/
│       │   ├── auth/           # AuthLayout
│       │   ├── common/         # Logo, LoadingSpinner, ProtectedRoute
│       │   ├── layout/         # AppLayout, Sidebar
│       │   └── ui/             # FormField, SectionHeader
│       ├── context/            # AuthContext (React Context + localStorage)
│       ├── hooks/               # useProfile (profile/password/avatar/CV mutations)
│       ├── pages/
│       │   ├── auth/           # LoginPage, RegisterPage
│       │   ├── dashboard/      # DashboardPage
│       │   ├── profile/        # ProfilePage
│       │   └── interview/      # CreateInterviewPage, InterviewPlaceholderPage
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
docker compose up -d
```

Verify it's running:
```bash
docker compose ps
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

> ✅ Spring Boot will auto-create/update the `users` and `interviews` tables via Hibernate `ddl-auto: update`

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

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "adam@example.com",
  "password": "securepassword"
}
```

### User Endpoints (Protected)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/users/me` | Get current user |
| `PUT` | `/users/me` | Update profile (name, email, target role, experience, language, country) |
| `PUT` | `/users/me/password` | Change password |
| `PUT` | `/users/me/avatar` | Update avatar (Base64 data-URL, max 2MB) |
| `PUT` | `/users/me/cv` | Update CV text (max 50,000 characters) |

### Interview Endpoints (Protected)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/interviews` | Create a new interview session |
| `GET` | `/interviews` | List the current user's interviews |
| `GET` | `/interviews/{id}` | Get a single interview (ownership-checked) |
| `GET` | `/interviews/stats/dashboard` | Aggregated stats: totals, avg/best score, 5 most recent |

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
- **Ownership checks** — interviews are scoped to the authenticated user; accessing another user's interview returns 403
- **Global exception handler** — never leaks stack traces to clients
- **Stateless sessions** — no server-side session storage

---

## 📊 Database Schema

```sql
CREATE TABLE users (
  id                  BIGSERIAL PRIMARY KEY,
  first_name          VARCHAR(50)  NOT NULL,
  last_name           VARCHAR(50)  NOT NULL,
  email               VARCHAR(100) NOT NULL UNIQUE,
  password            VARCHAR(255) NOT NULL,
  role                VARCHAR(20)  NOT NULL DEFAULT 'USER',
  target_role         VARCHAR(100),
  experience_level    VARCHAR(20),
  years_of_experience INTEGER,
  preferred_language  VARCHAR(20)  DEFAULT 'ENGLISH',
  target_country      VARCHAR(50),
  avatar_base64       TEXT,
  cv_text             TEXT,
  created_at          TIMESTAMP    NOT NULL,
  updated_at          TIMESTAMP    NOT NULL
);

CREATE TABLE interviews (
  id                  BIGSERIAL PRIMARY KEY,
  user_id             BIGINT       NOT NULL REFERENCES users(id),
  target_role         VARCHAR(100) NOT NULL,
  experience_level    VARCHAR(20)  NOT NULL,
  duration_minutes    INTEGER      NOT NULL DEFAULT 30,
  question_count      INTEGER,
  language            VARCHAR(20)  NOT NULL DEFAULT 'ENGLISH',
  status              VARCHAR(20)  NOT NULL DEFAULT 'CREATED',
  score               INTEGER,
  overall_feedback    TEXT,
  ai_prompt_snapshot  TEXT,
  created_at          TIMESTAMP    NOT NULL,
  updated_at          TIMESTAMP    NOT NULL,
  completed_at        TIMESTAMP
);

CREATE TABLE interview_technologies (
  interview_id BIGINT NOT NULL REFERENCES interviews(id),
  technology   VARCHAR(50)
);
```

> Schema is auto-managed by Hibernate. No manual SQL needed for development.

---

## 🗺️ Project Roadmap

| Sprint | Focus | Status |
|---|---|---|
| **Sprint 1** | Architecture, Auth, JWT, UI Foundation | ✅ **Done** |
| **Sprint 2** | Dashboard, User Profile, Interview Setup | ✅ **Done** |
| Sprint 3 | Gemini API, Question Generation | 🔜 Pending |
| Sprint 4 | AI Evaluation, Scoring, Feedback | 🔜 Pending |
| Sprint 5 | History, Statistics, PDF Reports | 🔜 Pending |
| Sprint 6 | UI/UX Polish, Animations, Dark Mode | 🔜 Pending |
| Sprint 7 | Testing, Deployment, Optimization | 🔜 Pending |

---

## 💡 Design Decisions

**Why React Context over Redux?**
Sprint 1 only needed simple auth state. Redux would be over-engineering at this stage. Context scales fine until Sprint 4–5 when we can evaluate if Redux/Zustand is needed.

**Why JWT stored in localStorage?**
Simpler DX for development. For production (Sprint 7), we'll move to HttpOnly cookies to prevent XSS token theft.

**Why `ddl-auto: update` instead of Flyway?**
Migrations (Flyway/Liquibase) add valuable structure but slow down early sprints. We'll introduce Flyway in Sprint 3 when the schema stabilizes.

**Why BCrypt cost 12?**
Industry standard is 10–12. Cost 12 takes ~300ms to hash — slow enough to defeat brute force, fast enough to not impact UX.

**Why store avatar/CV directly in the database (Base64/text)?**
Sprint 2 doesn't need a file server yet. Storing the avatar as a Base64 data-URL and the CV as plain text keeps the stack simple — no S3/object storage dependency until Sprint 7, if needed.

**Why extract CV text on the client instead of parsing PDFs on the backend?**
Avoids pulling in a binary PDF-parsing dependency before it's needed. The frontend reads the file and sends plain text; the backend just validates and stores it. Sprint 7 can swap in `pdf.js`-based extraction without changing the API contract.

**Why compute `questionCount` at interview creation instead of in Sprint 3?**
So the Gemini integration (Sprint 3) can read a ready-made value off the entity instead of recomputing it. Formula: 1 question per 5 minutes of planned duration, clamped to 3–15.

---

## 🤝 Contributing

This project is developed Sprint by Sprint with mentor guidance. Each Sprint is a self-contained feature set, reviewed before proceeding.

---

## 📄 License

MIT License.

---

<div align="center">
Built with ❤️ using Spring Boot, React, and Google Gemini AI
</div>
