# Pharmacy Drug Inventory System — CA235 Group 13

Full-stack project: Spring Boot + ReactJS + PostgreSQL + JWT security.

## Tech Stack
- Backend: Spring Boot 3.3 (Java 17, Maven)
- Security: Spring Security + JWT (jjwt)
- Database: PostgreSQL
- Frontend: React + Vite (`../pharmacy-frontend`)

## Project Structure
```
src/main/java/com/pharmacy/inventory/
  config/       → SecurityConfig
  security/     → JwtUtil, JwtAuthFilter, CustomUserDetailsService
  model/        → JPA entities (User, Medicine, Supplier, Prescription, Sale...)
  repository/   → Spring Data JPA interfaces
  dto/          → request/response objects
  service/      → business logic
  controller/   → REST endpoints
  exception/    → ApiException, GlobalExceptionHandler
```

## Getting Started

### 1. Start PostgreSQL
```bash
docker compose up -d
```
Runs on `localhost:5433` → DB: `pharmacy_db`, user: `pharmacy_user`, password: `pharmacy_pass`

### 2. Run the backend
```bash
./mvnw spring-boot:run
```
API runs on `http://localhost:8080`. Tables auto-create on first run.

### 3. Test the Auth module
Register:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","email":"admin1@pharmacy.com","password":"password123","role":"ADMIN"}'
```

Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","password":"password123"}'
```
Use the returned token on protected endpoints:
```
Authorization: Bearer <token>
```

### Other endpoints
- `GET /api/medicines` / `GET /api/medicines/{id}` — list medicines (read-only here; full CRUD lives on the medicine-stock module)
- `POST /api/prescriptions`, `GET /api/prescriptions`, `GET /api/prescriptions/{id}` — prescription records
- `POST /api/sales`, `GET /api/sales` — point-of-sale checkout (decrements medicine stock)
- `GET /api/sales/reports/monthly?year=2026&month=7` — monthly sales report

## Conventions
- Controllers return `ApiResponse<T>` for consistency.
- Throw `ApiException(message, HttpStatus)` for expected errors — handled globally.

## Roles
- `ADMIN` — full access
- `PHARMACIST` — day-to-day stock, sales, prescriptions
