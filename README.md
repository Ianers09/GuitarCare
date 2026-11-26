# GuitarCare — Guitar Maintenance and Service Tracking System

GuitarCare is a web application for guitar owners to organize their instruments, record maintenance activities, review service history, and plan upcoming maintenance.

## Technology

- Frontend: React 19, Vite, React Router, Axios
- Backend: Java 21, Spring Boot, Spring Data JPA, Spring Security
- Database: Supabase PostgreSQL
- Optional photo storage: Cloudinary unsigned image upload
- Calendar handoff: Google Calendar event-template link (not OAuth/API synchronization)

## Implemented functionality in this source package

### Accounts
- Registration and login using the backend session.
- Logout and protected frontend routes.
- BCrypt password hashing in the backend.
- Profile and password-management screens are present from the earlier increments; verify the configured email/reset workflow in your environment before claiming it as fully operational.

### Guitar and maintenance core
- Create, list, update, and delete guitars owned by the signed-in user.
- Create, view, edit, and delete maintenance activities for a selected guitar.
- View a guitar's maintenance history.
- Create, list, edit, update status, and delete future maintenance schedules.
- View upcoming schedules/reminder data for the next 30 days.
- Optional photo upload for guitar and maintenance records when Cloudinary is configured.
- Open a Google Calendar event template for a schedule.

## Important integration status

- Cloudinary uploads require a Cloudinary cloud name and unsigned upload preset in the frontend environment.
- The Google Calendar action opens a pre-filled event template. OAuth-based automatic synchronization with Google Calendar is not implemented in this package.
- Maintenance records and schedules support create, read, update, and delete through the UI and owner-scoped backend endpoints.
- The Increment 3 report documents external integrations and workflows that still require configuration or verification. Do not describe them as completed until implemented and verified.

## Requirements

- Java 21
- Node.js 20 or newer
- npm
- Supabase PostgreSQL project

## Database setup

Apply the SQL migrations in `supabase/migrations/` to your Supabase project in filename order:

1. `20261119000100_create_app_users.sql`
2. `20261124000100_create_password_reset_tokens.sql`
3. `20261126000100_create_guitarcare_core.sql`

You can run them using the Supabase SQL Editor. Keep database credentials on the backend only. Never place the database password or service-role key in frontend variables.

## Configure the backend (PowerShell)

Copy `backend/.env.example` as a reference; Spring Boot reads the following environment variables from the process environment. From the repository root:

```powershell
cd backend
$env:SUPABASE_DB_URL = "jdbc:postgresql://YOUR_POOLER_HOST:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.YOUR_PROJECT_REF"
$secure = Read-Host "Supabase database password" -AsSecureString
$env:SUPABASE_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password
./mvnw.cmd spring-boot:run
```

Backend API: `http://localhost:8080`  
Health check: `http://localhost:8080/api/health`

## Configure and run the frontend

Open a second PowerShell terminal from the repository root:

```powershell
cd frontend
Copy-Item .env.example .env
npm install
npm run dev
```

Open the local URL printed by Vite (normally `http://localhost:5173`). The Vite development server proxies `/api` to the backend.

### Optional Cloudinary configuration

Set these values in `frontend/.env` using your own Cloudinary account and an unsigned upload preset:

```env
VITE_CLOUDINARY_CLOUD_NAME=your_cloud_name
VITE_CLOUDINARY_UPLOAD_PRESET=your_unsigned_upload_preset
```

Restart Vite after editing `.env`. Never put private Cloudinary API secrets in frontend variables.

## Build and test

Frontend:

```powershell
cd frontend
npm install
npm run build
npm run lint
```

Backend (from `backend/`):

```powershell
./mvnw.cmd test
```

These commands should be run in the configured development environment. External services and credentials are required for full end-to-end verification.

## Documentation

- `docs/Increment-2-Completion-Report.pdf`
- `docs/Increment-3-Completion-Report.pdf`
- `docs/Increment-3-Completion-Report.docx` (editable report)

## Security and repository hygiene

- Do not commit `.env` files, database passwords, email app passwords, tokens, or private API keys.
- Use a rotated database password if an earlier credential was exposed.
- Each user can access only their own guitar and associated maintenance/schedule records through the authenticated session.
