# GuitarCare

GuitarCare is a React + Vite frontend with a Spring Boot REST backend and Supabase PostgreSQL database.

## Requirements
- Java 21
- Node.js 20 or newer
- Supabase PostgreSQL credentials
- SMTP account for email password recovery

## Backend setup (PowerShell)
```powershell
cd backend
$env:SUPABASE_DB_URL = "jdbc:postgresql://YOUR_POOLER_HOST:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME = "postgres.YOUR_PROJECT_REF"
$secure = Read-Host "Supabase database password" -AsSecureString
$env:SUPABASE_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password
$env:MAIL_HOST = "smtp.gmail.com"
$env:MAIL_PORT = "587"
$env:MAIL_USERNAME = "your-email@gmail.com"
$secureMail = Read-Host "Google app password" -AsSecureString
$env:MAIL_PASSWORD = [System.Net.NetworkCredential]::new("", $secureMail).Password
$env:APP_PUBLIC_URL = "http://localhost:5174"
.\mvnw.cmd spring-boot:run
```
Use the Supabase **session pooler** host, port 5432, database `postgres`, and username shown in Supabase Connect. Never commit real credentials. For Gmail, use a Google App Password; do not use your normal account password.

## Frontend setup (second terminal)
```powershell
cd frontend
npm install
npm run dev -- --port 5174
```
Open http://localhost:5174.

## Increment 2 features
- Profile view/update: `GET/PUT /api/account/profile`; server derives the account from the authenticated application session.
- Change password: `POST /api/account/change-password`; checks current password, confirmation and BCrypt-hashes the replacement. The user must sign in again.
- Forgot password: `POST /api/account/forgot-password`; sends a 30-minute single-use reset link. Responses do not disclose whether an email exists.
- Reset password: `POST /api/account/reset-password`; validates token expiry/use, hashes new password and marks token used.
- Password reset tokens are stored as SHA-256 hashes, not raw tokens.

The backend uses Hibernate schema update for local development. The included Supabase migration creates the password reset token table if migrations are managed through the Supabase CLI. Do not run both migration and Hibernate against conflicting schema changes; the SQL table definition matches the entity.

## Security and deployment notes
- Profile endpoints never accept a user ID from the browser; the server uses the session identity.
- Keep SMTP and database credentials in environment variables.
- Configure `APP_PUBLIC_URL` to the real HTTPS application URL before deployment.
- The Vite proxy routes `/api` requests to `http://localhost:8080` in development.
