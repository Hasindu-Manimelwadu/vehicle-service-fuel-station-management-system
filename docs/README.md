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

## Option B — Run each piece manually

Use this if you don't have Docker, or want to run things individually while
developing.

### 1. Database setup

Install MySQL locally (or use one already running), then:

```bash
mysql -u root -p < database/schema.sql
```

This creates the `vsfsms_staff_module` database with the `staff`,
`work_shift`, `attendance`, and `salary` tables, plus two sample staff rows.

### 2. Backend setup (Java / Spring Boot)

Requirements: **Java 17+** and **Maven**.

1. Open `backend/src/main/resources/application.properties` and set your
   MySQL username/password:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```
2. Run it:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
3. The API starts on **http://localhost:8080**. Quick check:
   ```
   GET http://localhost:8080/api/staff
   ```

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

## Notes / things you may want to extend

- `userId` on Staff is a plain column (not a foreign key) since the `User`
  table belongs to the Authentication module owned by another team member.
  Once that module's schema is finalized, add the FK in `schema.sql`.
- CORS is currently wide open (`allowedOriginPatterns("*")`) for local
  development — tighten this before deploying anywhere shared.
- There's no authentication on these endpoints yet — they assume an
  Administrator is already logged in via the shared Auth module. If you need
  to gate access, that's the natural place to add a Spring Security filter.
