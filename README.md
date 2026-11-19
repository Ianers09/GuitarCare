# GuitarCare

GuitarCare is a React + Vite frontend with a Spring Boot REST API and Supabase PostgreSQL database.

## Current functionality

- Account registration with server-side validation.
- BCrypt password hashing (raw passwords are never stored).
- Login using the registered email and password.
- Server-managed HTTP session; the browser receives an HttpOnly session cookie.
- Cookie-based CSRF protection for state-changing requests.
- Session restoration on page refresh, protected dashboard route, and logout.
- Supabase database migration for the `public.app_users` table.

The dashboard currently provides an authenticated starting workspace. Guitar inventory and maintenance CRUD features are not present in the supplied source project and have not been represented as completed functionality.

## Requirements

- Java 21
- Node.js 20 or newer
- A Supabase PostgreSQL project

## Database setup

The migration is in `supabase/migrations/20261119000100_create_app_users.sql`.

If the Supabase GitHub integration is enabled for this repository with working directory `.` and production branch `main`, push this migration to `main` and check the integration deployment logs in Supabase. Alternatively, apply the SQL in the Supabase SQL Editor. The migration is idempotent for table creation.

The application connects to PostgreSQL using the Supabase **Session pooler** connection details. The Spring Boot API uses the database role, so the browser must never receive the database password or a Supabase service-role key.

## Run the backend (PowerShell)

From the repository root:

```powershell
cd backend
$env:SUPABASE_DB_URL = "jdbc:postgresql://YOUR-POOLER-HOST:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.YOUR-PROJECT-REF"
$env:SUPABASE_DB_PASSWORD = Read-Host "Supabase database password"
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`. Check `http://localhost:8080/api/health` for its health response.

## Run the frontend

Open a second terminal from the repository root:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/api` requests to the Spring Boot backend.

## Authentication API

- `POST /api/auth/register` — `{ "fullName": "...", "email": "...", "password": "..." }`
- `POST /api/auth/login` — `{ "email": "...", "password": "..." }`
- `GET /api/auth/me` — returns the current session user or HTTP 401.
- `POST /api/auth/logout` — invalidates the current session.

Keep secrets in local environment variables. Do not commit `.env` files or database credentials.
