# Inventory Manager

A full-stack inventory management web application for small-to-medium retail and warehouse operations. Built with a **Spring Boot** REST API and a **React** single-page application, it gives teams a structured interface to track products, suppliers, orders, and stock levels — with role-based access so admins manage everything while employees can browse and create orders.

---

## What it does

The application solves the day-to-day operational needs of a stocked business:

- **Know what you have** — Products are organised by category with a price, current stock quantity, and a per-product low-stock threshold. Items that fall below their threshold are flagged in red across the UI and counted on the dashboard.
- **Know who supplies it** — Each supplier has contact details stored and linked to the orders placed with them.
- **Track incoming stock** — Orders are raised against a supplier and a product, then move through a status pipeline (`PENDING → CONFIRMED → DELIVERED`). When an order is marked delivered, the product's stock quantity is automatically incremented.
- **See the big picture** — A live dashboard shows four KPI cards (total products, low-stock items, pending orders, total suppliers) and a bar chart of current stock broken down by category.
- **Control who can do what** — Two roles (`Admin` and `Employee`) are enforced at both the API and UI layer. Employees can browse everything and raise orders; only admins can create, edit, or delete records and manage user roles.

---

## Tech stack

### Backend

| | |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Security | Spring Security — stateless JWT filter chain |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL 17 |
| JWT library | jjwt 0.11.5 (HS256) |
| Boilerplate | Lombok 1.18.38 |
| Build | Maven 3 |
| Local DB | Docker Compose |

### Frontend

| | |
|---|---|
| UI | React 18.3.1 |
| Routing | React Router DOM 6.26.2 |
| HTTP | Axios 1.7.9 |
| Charts | Recharts 2.12.7 |
| Styling | Tailwind CSS 3.4.14 |
| Build tool | Vite 8.0.16 |

---

## Project structure

```
Inventory_Manager/
├── backend/
│   ├── docker-compose.yaml              # PostgreSQL 17 container
│   ├── pom.xml
│   └── src/main/java/com/inventorymanager/
│       ├── auth/                        # Users, JWT, login, register, role management
│       │   ├── dto/                     # AuthResponse, LoginRequest, RegisterRequest,
│       │   │                            #   UserResponse, UpdateRoleRequest
│       │   ├── AuthController.java      # POST /api/auth/login|register
│       │   ├── UserController.java      # GET /api/users, PATCH /api/users/{id}/role
│       │   ├── AuthService / AuthServiceImpl
│       │   ├── JwtUtil.java
│       │   ├── User.java                # JPA entity → users table
│       │   └── UserRepository.java
│       ├── category/                    # Category CRUD
│       ├── product/                     # Product CRUD + low-stock query
│       ├── supplier/                    # Supplier CRUD
│       ├── order/                       # Order CRUD + status transitions + stock update
│       ├── dashboard/                   # KPI summary + category stock aggregation
│       ├── common/                      # ApiResponse, ErrorResponse, custom exceptions,
│       │                                #   GlobalExceptionHandler
│       └── config/                      # SecurityConfig, CorsConfig, JwtFilter
│
└── frontend/
    ├── index.html
    ├── vite.config.js
    ├── tailwind.config.js
    └── src/
        ├── api/                         # One module per domain + mockStore.js
        │   └── auth, categories, products, suppliers,
        │       orders, dashboard, users, client
        ├── components/                  # Badge, Button, ConfirmDialog, ErrorState,
        │                                #   Input, LoadingState, Modal, Navbar,
        │                                #   PageHeader, Sidebar, StatCard, Table
        ├── context/
        │   └── AuthContext.jsx          # Token + user state, isAdmin, isEmployee
        ├── layouts/
        │   └── AppLayout.jsx            # Sidebar + Navbar shell
        ├── pages/
        │   ├── auth/                    # LoginPage, RegisterPage
        │   ├── dashboard/               # DashboardPage
        │   ├── products/                # ProductsPage, ProductModal
        │   ├── categories/              # CategoriesPage, CategoryModal
        │   ├── suppliers/               # SuppliersPage, SupplierModal
        │   ├── orders/                  # OrdersPage, OrderModal
        │   └── users/                   # UsersPage (Admin only)
        ├── routes/
        │   └── ProtectedRoute.jsx       # Auth guard + optional role guard
        ├── utils/
        │   └── format.js
        ├── constants.js                 # USE_MOCK toggle, token keys
        └── App.jsx                      # Route tree
```

---

## Roles and permissions

| Action | Admin | Employee |
|---|---|---|
| View dashboard | ✅ | ✅ |
| View products / categories / suppliers | ✅ | ✅ |
| Create / edit / delete products | ✅ | ❌ |
| Create / edit / delete categories | ✅ | ❌ |
| Create / edit / delete suppliers | ✅ | ❌ |
| View orders | ✅ | ✅ |
| Create an order | ✅ | ✅ |
| Confirm / deliver / delete an order | ✅ | ❌ |
| View and manage users | ✅ | ❌ (route hidden) |

Roles are stored in the `users` table, embedded in the JWT as a `role` claim, extracted by `JwtFilter` on every request, and enforced by `@PreAuthorize` on each controller method. The frontend additionally hides all write buttons and the Users sidebar link for employees, and `ProtectedRoute` redirects employees away from `/users` if they navigate there directly.

---

## API reference

All endpoints except `/api/auth/**` require `Authorization: Bearer <token>`.

Every response uses this envelope:

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": {}
}
```

Errors return:

```json
{
  "status": 403,
  "message": "Access denied: insufficient permissions",
  "timestamp": "2026-06-02T14:00:00"
}
```

### Auth

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Login — returns JWT + user info |
| POST | `/api/auth/register` | Public | Register — choose role on the form |

### Users

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/users` | Admin | List all users |
| PATCH | `/api/users/{id}/role` | Admin | Change a user's role |

### Products

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/products` | All | List all products |
| GET | `/api/products/{id}` | All | Get one product |
| GET | `/api/products/low-stock?threshold=N` | All | Products below threshold |
| POST | `/api/products` | Admin | Create product |
| PUT | `/api/products/{id}` | Admin | Update product |
| DELETE | `/api/products/{id}` | Admin | Delete product |

### Categories

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/categories` | All | List all categories |
| GET | `/api/categories/{id}` | All | Get one category |
| POST | `/api/categories` | Admin | Create category |
| PUT | `/api/categories/{id}` | Admin | Update category |
| DELETE | `/api/categories/{id}` | Admin | Delete category |

### Suppliers

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/suppliers` | All | List all suppliers |
| GET | `/api/suppliers/{id}` | All | Get one supplier |
| POST | `/api/suppliers` | Admin | Create supplier |
| PUT | `/api/suppliers/{id}` | Admin | Update supplier |
| DELETE | `/api/suppliers/{id}` | Admin | Delete supplier |

### Orders

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/orders` | All | List orders — filter by `?status=PENDING\|CONFIRMED\|DELIVERED` |
| GET | `/api/orders/{id}` | All | Get one order |
| POST | `/api/orders` | All | Create order (status starts as PENDING) |
| PATCH | `/api/orders/{id}/status` | Admin | Update order status |
| DELETE | `/api/orders/{id}` | Admin | Delete order |

### Dashboard

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/api/dashboard/summary` | All | KPI totals |
| GET | `/api/dashboard/category-stock` | All | Stock quantity per category |

---

## Getting started

### Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+**
- **Docker and Docker Compose**

---

### Step 1 — Start the database

From the `backend/` folder, spin up the PostgreSQL container:

```bash
cd backend
docker compose up -d
```

This starts PostgreSQL 17 on port `5432` with database `inventorydb`, user `postgres`, password `password`. Hibernate creates all tables automatically on first boot (`ddl-auto=update`), so no SQL scripts are needed.

Verify it is running:

```bash
docker ps
# should show: inventory-postgres   Up
```

---

### Step 2 — Run the backend

Still in the `backend/` folder:

```bash
mvn clean package -DskipTests
java -jar target/inventory-manager-0.0.1-SNAPSHOT.jar
```

The API starts on **http://localhost:8080**.

You should see Spring Boot's startup log ending with `Started InventoryManagerApplication`.

Alternatively, open the project in IntelliJ IDEA and run `InventoryManagerApplication.java` directly.

---

### Step 3 — Run the frontend

In a new terminal, from the `frontend/` folder:

```bash
cd frontend
npm install
npm run dev
```

The app opens on **http://localhost:3000**.

---

### Step 4 — Create your first account

1. Open **http://localhost:3000/register**
2. Fill in your name, email, and password
3. Select **Admin** from the Role dropdown
4. Click **Create account**
5. You will be redirected to the login page — sign in with the same credentials

You will land on the dashboard. As an Admin you have full access to all pages including **Users** in the sidebar.

> To create an Employee account, repeat the steps above and select **Employee** from the dropdown. Employees see the same data but cannot create, edit, or delete any records, and the Users page is hidden from them.

---

## Configuration

### Backend — `application.properties`

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/inventorydb
spring.datasource.username=postgres
spring.datasource.password=password

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT — change the secret before deploying to production
jwt.secret=change-this-secret-key-please-use-a-longer-secret-key-1234567890
jwt.expiration=86400000
```

The JWT expiration is in milliseconds. `86400000` = 24 hours.

**Before deploying to production**, replace the hardcoded secret with an environment variable:

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

Then set `JWT_SECRET` in your server environment or secrets manager.

### Frontend — `.env`

Copy the example file and set your backend URL:

```bash
cp .env.example .env
```

```env
VITE_API_URL=http://localhost:8080/api
```

If you deploy the backend to a server, change this value to the production URL and rebuild the frontend with `npm run build`.

### Mock mode

The frontend includes a full in-memory mock store for offline development. To enable it, open `frontend/src/constants.js` and set:

```js
export const USE_MOCK = true;
```

With mock mode on, no backend or database is needed. All data lives in the browser and resets on page refresh. Switch back to `false` to connect to the real API.

---

## How the security works

```
Request arrives
     │
     ▼
JwtFilter (OncePerRequestFilter)
  └── reads Authorization: Bearer <token>
  └── validates signature + expiry via JwtUtil
  └── extracts email + role from JWT claims
  └── sets UsernamePasswordAuthenticationToken in SecurityContext
     │
     ▼
SecurityConfig
  └── /api/auth/** → permitAll (no token needed)
  └── everything else → authenticated()
     │
     ▼
Controller method
  └── @PreAuthorize("isAuthenticated()") → any logged-in user
  └── @PreAuthorize("hasAuthority('ROLE_ADMIN')") → admins only
     │
     ▼
GlobalExceptionHandler
  └── AccessDeniedException  → 403 JSON response
  └── EntityNotFoundException → 404 JSON response
  └── UnauthorizedException  → 401 JSON response
  └── ConflictException      → 409 JSON response
```

Passwords are hashed with `BCryptPasswordEncoder`. Tokens are signed with HMAC-SHA256. Sessions are fully stateless — no cookies, no server-side session storage.

---

## Deployment

The application is deployed on an **Oracle Cloud Infrastructure (OCI)** Ubuntu instance, running alongside other services without conflicts. All three containers (PostgreSQL, backend, frontend) are isolated using Docker and share a common internal bridge network (`web-network`) with Nginx Proxy Manager, which handles all inbound traffic. No host ports are exposed — containers are only reachable through the reverse proxy.

Public access is routed via **Cloudflare Tunnels**, which handle DNS, SSL termination, and HTTPS automatically:

| Service | URL |
|---------|-----|
| Frontend | https://inventory-manager.tahableu.me |
| Backend API | https://api-inventory-manager.tahableu.me |

Nginx Proxy Manager forwards requests to the containers by name (`inventory-frontend:80` and `inventory-backend:8080`) over the internal Docker network — no IP addresses or open firewall ports required.

The production `docker-compose.yaml` differs from the local one in three ways: all `ports:` mappings are replaced with `expose:` (internal only), the frontend build arg `VITE_API_URL` is set to the production API domain, and the Docker network is declared as `external: true` to attach to the shared `web-network`.

---

## CI/CD pipeline

The pipeline is defined in `.github/workflows/ci.yml` and triggers automatically on every push or pull request to the `main` branch. It runs four jobs in sequence:

```
push to main
     │
     ├─► Backend — Build & Test   ─┐
     │   (Maven clean verify)      ├─► Docker — Build & Push ──► Deploy — OCI Server
     └─► Frontend — Build         ─┘   (GHCR)                    (SSH)
         (npm ci + npm run build)
```

### Job 1 & 2 — Validate (parallel)

The backend job runs `mvn -B clean verify`, which compiles the code and executes the full test suite including unit tests and integration tests. The frontend job runs `npm ci` followed by `npm run build` to verify the React app compiles cleanly. Both jobs run in parallel on GitHub-hosted Ubuntu runners. If either fails, the pipeline stops and no deployment occurs.

### Job 3 — Docker Build & Push

Once both validation jobs pass, Docker images are built for the backend and frontend and pushed to **GitHub Container Registry** (`ghcr.io`) using the built-in `GITHUB_TOKEN` — no external registry account needed. Each image is tagged with both the commit SHA (for traceability) and `latest` (for the deploy step to pull).

### Job 4 — Deploy

The deploy job SSH-es directly into the OCI server using three repository secrets:

| Secret | Purpose |
|--------|---------|
| `OCI_HOST` | Server public IP |
| `OCI_USER` | SSH username (`ubuntu`) |
| `OCI_SSH_KEY` | Private SSH key (ED25519) |

No self-hosted runner or agent is installed on the server. GitHub's own runners initiate the connection using the `appleboy/ssh-action`, then execute the following on the server:

```bash
cd ~/main-server/inventory-manager
git pull origin main
docker compose pull
docker compose up -d --build
docker image prune -f
```

This pulls the latest code, rebuilds the containers with the new images, restarts only the affected services with zero-downtime rolling replacement, and cleans up dangling images to free disk space.

### Adding the pipeline to a fork

To use the CD pipeline in your own deployment:

1. Fork the repository
2. Go to **Settings → Secrets and variables → Actions**
3. Add the three secrets: `OCI_HOST`, `OCI_USER`, `OCI_SSH_KEY`
4. Update the `VITE_API_URL` build arg in `docker-compose.yaml` to your own domain
5. Push to `main` — the pipeline runs automatically

---

## Using the application

### Managing stock

1. Go to **Categories** and create at least one category (e.g. "Beverages")
2. Go to **Suppliers** and add a supplier
3. Go to **Products**, click **Add Product**, fill in name, price, stock quantity, low-stock threshold, and category
4. Products below their threshold appear highlighted in red with a **Low Stock** badge
5. The dashboard automatically reflects the updated counts

### Placing and fulfilling an order

1. Go to **Orders** and click **New Order**
2. Select a supplier, a product, and a quantity — the order is saved with status `PENDING`
3. When the stock arrives, an Admin opens the order and changes the status to `CONFIRMED`, then `DELIVERED`
4. On delivery the product's stock quantity is automatically increased by the order quantity

### Managing users (Admin only)

1. Go to **Users** in the sidebar
2. The table lists every registered account with their current role
3. Click **Make Admin** or **Make Employee** to change a user's role — a confirmation dialog appears before the change is applied

---

## Available scripts

### Backend

```bash
# Build and run
mvn clean package -DskipTests
mvn -e spring-boot:run

# Run tests
mvn test

# Start database only
docker compose up -d

# Stop database
docker compose down
```

### Frontend

```bash
# Install dependencies
npm install

# Start dev server (http://localhost:3000)
npm run dev

# Build for production
npm run build

# Preview production build locally
npm run preview
```