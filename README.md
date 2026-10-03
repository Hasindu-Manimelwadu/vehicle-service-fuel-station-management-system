# Staff Management Module — VSFSMS

Owner: **Abeykoon A.M.H.S. (IT25100971)** — Staff Management component of the
Vehicle Service and Fuel Station Management System.

Covers full CRUD on four entities: **Staff, Work Shift, Attendance, Salary**.

```
staff-management/
├── docker-compose.yml       spins up all three services wired together
├── database/
│   └── schema.sql          MySQL schema + sample data
├── backend/                Java (Spring Boot) REST API
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/vsfsms/staffmodule/
│       ├── model/          JPA entities
│       ├── repository/     Spring Data repositories
│       ├── service/        business logic (validation, ID generation, net-salary calc)
│       ├── controller/     REST endpoints
│       ├── exception/      custom exceptions + GlobalExceptionHandler (JSON errors)
│       └── config/         CORS config
└── frontend/                Plain HTML / CSS / JS admin panel
    ├── index.html
    ├── style.css
    ├── app.js
    └── nginx.conf           proxies /api/* to the backend container
```

## Option A — Docker Compose (recommended: connects all three automatically)

Requires only **Docker Desktop** (no local Java, Maven, or MySQL install needed).

```bash
cd staff-management
docker compose up --build
```

This does everything in one command:
1. Starts **MySQL 8**, auto-runs `database/schema.sql` on first boot (creates
   tables + sample rows).
2. Builds and starts the **Spring Boot backend**, already configured (via
   environment variables in `docker-compose.yml`) to connect to that MySQL
   container — no manual editing of `application.properties` needed.
3. Serves the **frontend** through nginx, which also reverse-proxies
   `/api/*` requests straight to the backend container — so the browser only
   ever talks to one origin.

Once it's up:
- Admin panel: **http://localhost:8081**
- API directly: **http://localhost:8080/api/staff**
- MySQL (if you want to connect a client): **localhost:3306**, user `root`,
  password `staffpass123` (change this in `docker-compose.yml` for anything
  beyond local dev)

Stop everything with `docker compose down` (add `-v` to also wipe the
database volume and start fresh next time).

## Option B — Run in IntelliJ with the H2 database (no MySQL needed)

When you run the backend directly (IntelliJ / `mvn spring-boot:run`) it uses
an **H2** database that is created automatically — nothing to install, no
MySQL password to set. Data is stored in `~/vsfsms/staffdb.mv.db` in your
home folder, so it survives restarts. Two sample staff members and today's
shifts are added the first time.

1. Run `StaffModuleApplication` (green ▶) — Java 17+ required.
2. Wait for `Started StaffModuleApplication` in the console.
3. Open the admin panel: `frontend/index.html`
   (it connects to `http://localhost:8080/api` automatically).

### H2 Console (view/query the database in your browser)

Open **http://localhost:8080/h2-console** and log in with:

| Setting  | Value |
|----------|-------|
| Driver   | `org.h2.Driver` (default) |
| JDBC URL | `jdbc:h2:file:~/vsfsms/staffdb;NON_KEYWORDS=MONTH,YEAR,DAY,VALUE,KEY;AUTO_SERVER=TRUE` |
| User     | `sa` |
| Password | `staffpass123` |

Try `SELECT * FROM STAFF;` or `SELECT * FROM SALARY;`.
To start with a fresh database, stop the app and delete the `~/vsfsms` folder
(on Windows: `C:\Users\<you>\vsfsms`).

Docker (Option A) still uses MySQL — `docker-compose.yml` sets
`SPRING_PROFILES_ACTIVE=mysql`, which loads `application-mysql.properties`.

### Endpoints (same CRUD shape for all four entities)

| Entity      | Base path         |
|-------------|--------------------|
| Staff       | `/api/staff`       |
| Work Shift  | `/api/shifts`      |
| Attendance  | `/api/attendance`  |
| Salary      | `/api/salaries`    |

Each supports:
- `GET    /api/staff`          — list all
- `GET    /api/staff/{id}`     — get one
- `POST   /api/staff`          — create
- `PUT    /api/staff/{id}`     — update
- `DELETE /api/staff/{id}`     — delete

`shifts`, `attendance` and `salaries` also accept `?staffId=STF001` on the
list endpoint to filter by staff member.

IDs (staffId, shiftId, etc.) are auto-generated server-side if you leave
them blank on create.

### 3. Frontend (admin panel)

No build step — it's plain HTML/CSS/JS. Just open it in a browser:

```bash
cd frontend
open index.html      # macOS
# or just double-click index.html
```

Make sure the backend is running first. The top-left field in the sidebar
("API base URL") defaults to `http://localhost:8080/api` — change it there
if your backend runs elsewhere. The sidebar also shows a live
connected/not-connected indicator.

The panel has four tabs — **Staff, Work Shifts, Attendance, Salary** — each
with a table of records plus **Add / Edit / Delete**. Salary's net amount
(basic + overtime − deductions) is calculated automatically by the backend
whenever you create or update a salary record.

## Validation

Validation runs in three layers: the admin panel (instant, inline messages),
the Spring Boot API (the real authority — never trust the browser), and
MySQL CHECK/UNIQUE/FK constraints as a last safety net.

| Entity | Rules |
|---|---|
| Staff | Employee no `EMP-` + 4–6 digits, unique · full name 3–100 letters/spaces/`.'-` · Sri Lankan phone (`0XXXXXXXXX` or `+94XXXXXXXXX`) · valid email, unique (case-insensitive) · designation 2–50 chars · date joined not in future · basic salary > 0, max 2 decimals |
| Work shift | Staff must exist and not be TERMINATED · date not before join date · new SCHEDULED shifts not in the past · end ≠ start (end < start = overnight) · max 16 h · no overlapping non-cancelled shifts for the same staff/day |
| Attendance | Staff must exist · date not in future or before join date · one record per staff per day · ABSENT/ON_LEAVE → no times, 0 h · PRESENT/LATE/HALF_DAY → check-in required · check-out after check-in · working hours auto-calculated |
| Salary | Staff must exist · month `YYYY-MM`, not in future or before join month · one record per staff per month · basic > 0, deductions ≥ 0 · overtime auto-calculated from attendance · deductions ≤ basic + overtime · PAID requires payment date · payment date not in future or before the salary month · net auto-calculated |

### Overtime (automatic)

Overtime is calculated from attendance — nobody types it in:

1. Each attendance record's working hours come from check-in/check-out. A
   check-out earlier than the check-in means the person worked past midnight
   (e.g. 22:00 → 06:00 = 8 h), the same way overnight work shifts work.
2. Hours beyond that day's **normal hours** count as overtime. Normal hours =
   the length of the staff member's scheduled (non-cancelled) shift(s) that
   day; if no shift is scheduled, the **standard day (8 h)** is used.
   The attendance table shows e.g. `10.50 +2.50 OT`.
3. A salary record's **overtime hours** = the total for that staff member's month.
4. **Overtime pay** = overtime hours × (basic salary ÷ **240**) × **1.5**.
5. **Net** = basic + overtime pay − deductions.

Example: basic Rs. 12,000 → hourly Rs. 50 → OT rate Rs. 75/h;
4 OT hours → Rs. 300 → net Rs. 12,300 (with no deductions).

Unpaid (PENDING / FAILED) salary records are kept in step automatically: they
are recalculated whenever attendance or shifts change, and also whenever
salaries are loaded, so a record created before its attendance was entered
picks up its overtime. **PAID records are never changed.** Days with a
check-in but no check-out are not counted until the check-out is entered.

Settings in `application.properties` (each overridable by environment
variable): `PAYROLL_STANDARD_DAILY_HOURS`, `PAYROLL_MONTHLY_HOURS_DIVISOR`,
`PAYROLL_OT_MULTIPLIER`, and `PAYROLL_USE_SHIFT_HOURS` (set to `false` to
always use the standard day instead of the shift length).

Extra endpoints: `GET /api/salaries/overtime-preview?staffId=STF001&month=2026-09`
and `GET /api/salaries/overtime-rules`.

All IDs are optional on create (auto-generated), must be unique, ≤ 20 chars.

### Error format

Every error comes back as JSON with an appropriate status (400 validation,
404 not found, 409 duplicate):

```json
{ "timestamp": "2026-09-30T10:15:00", "status": 400,
  "error": "Validation failed. Please correct the highlighted fields.",
  "fields": { "phone": "Phone must be a valid Sri Lankan number, e.g. 0771234567 or +94771234567" } }
```

Deleting a staff member also deletes their shifts, attendance and salary rows.
`GET /api/staff?search=text` searches name, employee no, email and designation.

> **Upgrading an existing Docker database:** `schema.sql` only runs on a fresh
> volume. To get the new CHECK constraints, run `docker compose down -v` then
> `docker compose up --build` (this wipes existing data).

## Notes / things you may want to extend

- `userId` on Staff is a plain column (not a foreign key) since the `User`
  table belongs to the Authentication module owned by another team member.
  Once that module's schema is finalized, add the FK in `schema.sql`.
- CORS is currently wide open (`allowedOriginPatterns("*")`) for local
  development — tighten this before deploying anywhere shared.
- There's no authentication on these endpoints yet — they assume an
  Administrator is already logged in via the shared Auth module. If you need
  to gate access, that's the natural place to add a Spring Security filter.
