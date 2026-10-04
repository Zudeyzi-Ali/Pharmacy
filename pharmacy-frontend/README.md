# Pharmacy Drug Inventory System — Frontend

React + Vite frontend. Covers authentication (login, register, protected routes),
prescriptions, sales (POS), and monthly sales reports.

## Getting Started

```bash
npm install
npm run dev
```

Runs on `http://localhost:5173`. Make sure the backend is running on `http://localhost:8080`
first (see the backend README).

## Structure

```
src/
  api/client.js          → axios instance, attaches JWT to every request automatically
  context/AuthContext.jsx → login/register/logout state, persisted in localStorage
  components/
    ProtectedRoute.jsx    → wrap any route that requires login (optionally restrict by role)
  pages/
    Login.jsx
    Register.jsx
    Dashboard.jsx
    Prescriptions.jsx
    Sales.jsx
    Reports.jsx
  styles/global.css        → design tokens (colors, type) + all component styles
```

## Adding a new page

1. Add your page under `src/pages/` (e.g. `Medicines.jsx`).
2. Wrap it in `<ProtectedRoute>` inside `App.jsx`, same pattern as `/dashboard`.
3. Use `apiClient` from `src/api/client.js` for all backend calls — it already attaches the
   JWT and redirects to `/login` automatically if the token is invalid/expired, so you don't
   need to handle auth headers yourself.
4. If a page should be restricted to a role, pass `allowedRoles={['ADMIN']}` to `ProtectedRoute`.

## Test accounts

Create one via the `/register` page.
