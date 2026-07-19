# MedVault Backend

Spring Boot API that replaces the hardcoded `USERS` object in `login.html` with
real authentication, a database, blockchain-anchored records, and IPFS pinning.

## What's included

| Piece | Where | What it does |
|---|---|---|
| Auth | `AuthController`, `AppUser` | Login checked against a real DB table, BCrypt-hashed passwords, role-checked (Patient/Doctor/Admin), issues a session token |
| Database | `application.properties` | H2 in-memory for local dev; commented Postgres block for production |
| Blockchain | `BlockchainService` | Same SHA-256 index+data+previousHash chaining as the original `Blockchain.java`, now persisted per block and verifiable with `GET /api/admin/chain/verify` |
| IPFS | `IPFSService` | Pins each record's payload to a Kubo/IPFS daemon and stores the returned CID; fails soft (returns `null`) if no node is reachable, so it never blocks the demo |

## Run it locally

```bash
mvn spring-boot:run
```

Starts on `http://localhost:8080`. Demo accounts are seeded automatically:

| username | password | role |
|---|---|---|
| patient1 | patient123 | Patient |
| patient2 | patient123 | Patient |
| doctor1  | doctor123  | Doctor  |
| admin1   | admin123   | Admin   |

Try it:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient1","password":"patient123","role":"Patient"}'
```

## Connect the existing frontend

Replace the hardcoded `const USERS = {...}` block in `login.html` with the code
in `frontend-integration/login.js` (set `API_BASE` to wherever the backend is
running), and point `dashboard.html`'s data fetches at:

- `GET /api/patient/records` — patient's own records
- `POST /api/patient/records` — upload a new record `{ diagnosis, doctorUsername }`
- `GET /api/doctor/patients` — doctor's patient list
- `PUT /api/doctor/records/{id}` — update a diagnosis `{ diagnosis }`
- `GET /api/admin/users` / `GET /api/admin/records` — admin views
- `GET /api/admin/chain/verify` — confirms the blockchain hasn't been tampered with

All protected routes expect `Authorization: Bearer <token>` from the login response.

## Add real IPFS

Install [Kubo](https://docs.ipfs.tech/install/command-line/) and run:

```bash
ipfs daemon
```

That exposes the API at `http://127.0.0.1:5001`, which `IPFSService` already
points at by default. For a hosted deployment where you can't run a daemon
alongside the app, use a pinning service's API (e.g. Pinata) and adjust
`IPFSService.addContent()` to call their endpoint + auth header instead.

## Deploy

**Database:** create a free Postgres instance on [Render](https://render.com) or
[Railway](https://railway.app), copy its connection URL, uncomment the Postgres
block in `application.properties`, and add the `postgresql` driver dependency
back in `pom.xml` (commented there too).

**Backend:** both Render and Railway can build straight from the included
`Dockerfile` — connect your GitHub repo, select "Docker" as the environment,
and set these environment variables:

```
DATABASE_URL=<from your Postgres instance>
DB_USER=<...>
DB_PASSWORD=<...>
IPFS_API_URL=<your pinning service or hosted IPFS node, if any>
```

**Frontend:** keep it on GitHub Pages/Netlify as already set up, just update
`API_BASE` in `login.js` to the deployed backend's URL. Watch for CORS — the
`SecurityConfig.corsConfigurer()` bean already allows all origins on `/api/**`
so this should work out of the box, but you can lock it down to your exact
frontend domain once it's stable.

## Note on real patient data

This setup (BCrypt passwords, token auth, DB-backed records) is solid for a
demo or coursework submission. If this is ever meant to hold real patients'
data, it still needs: HTTPS everywhere, audit logging of who accessed what,
encryption at rest for the database, a real password-reset flow, and — in the
US — HIPAA-compliant hosting agreements. Worth doing before any real diagnosis
data goes in, not after.
