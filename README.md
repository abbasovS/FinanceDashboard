# Finance Data Processing and Access Control Backend

A robust, production-ready Spring Boot RESTful API designed for a finance dashboard system. This project demonstrates core backend engineering principles including Role-Based Access Control (RBAC), complex data aggregation, secure stateless authentication, and clean architectural design.

## 🚀 Key Features & Best Practices
- **Stateless Security:** Secured via JWT (JSON Web Tokens).
- **Advanced RBAC:** Method-level security (`@PreAuthorize`) enforcing strict boundaries between `VIEWER`, `ANALYST`, and `ADMIN` roles.
- **Dynamic Filtering & Pagination:** Implemented `Pageable` and dynamic query parameters (e.g., date ranges, categories, transaction types) for efficient large-scale data retrieval.
- **Dashboard Aggregations:** Custom SQL/JPQL queries to calculate net balances, category-wise expense distributions, and 6-month financial trends.
- **Data Integrity:** Automated `createdAt` and `updatedAt` tracking using **Spring Data JPA Auditing**.
- **Soft Deletion:** Implemented soft-delete mechanism (`is_deleted` flag) to prevent accidental data loss while filtering out deleted records from queries.
- **Centralized Error Handling:** `@RestControllerAdvice` ensures consistent and structured JSON responses for all API exceptions (Validation, Unauthorized, Not Found, etc.).

## 🛠 Tech Stack
- **Java 21**
- **Spring Boot 3** (Web, Security, Data JPA, Validation)
- **H2 In-Memory Database** (for rapid local setup and assessment purposes)
- **Lombok** (to minimize boilerplate code)
- **io.jsonwebtoken (JJWT)** (for token generation and validation)
- **Docker** (Multi-stage build for lightweight production images)

## 👥 User Roles & Access Levels
| Role | Capabilities |
| :--- | :--- |
| **VIEWER** | Read-only. Can view their own profile and personal dashboard summaries. |
| **ANALYST** | Can view their own dashboard, read their own transaction history, and create new personal financial records. |
| **ADMIN** | Full system access. Can manage (CRUD) all users, manage all records, and view any user's dashboard. |

> **Default Admin Credentials (Auto-initialized on startup):**
> - **Username:** `admin`
> - **Password:** `admin123`

---

## 📡 API Endpoints Reference

### 🔐 1. Authentication
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | **Public** | Authenticates user and returns a JWT token. Expects `username` and `password`. |

### 👤 2. User Management
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/users/me` | `VIEWER, ANALYST, ADMIN` | Retrieve the current authenticated user's profile. |
| `GET` | `/api/v1/users` | `ADMIN` | Get a paginated list of all users. Supports `?page=0&size=10`. |
| `POST` | `/api/v1/users` | `ADMIN` | Create a new user account. |
| `PATCH`| `/api/v1/users/{id}/role` | `ADMIN` | Update a specific user's role. Requires `?role=ROLE_NAME`. |
| `PATCH`| `/api/v1/users/{id}/status`| `ADMIN` | Toggle a user's active/inactive status (Soft disable). |

### 📊 3. Dashboard & Analytics
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/dashboard/me` | `VIEWER, ANALYST, ADMIN` | Get aggregated metrics (Total Income, Total Expense, Net Balance, Category Totals, Monthly Trends, Top 5 Recent Activity) for the current user. |
| `GET` | `/api/v1/dashboard/users/{userId}`| `ADMIN` | Get aggregated dashboard metrics for any specific user. |

### 💰 4. Financial Records
*Note: All `GET` requests support pagination via `page`, `size`, and `sort` parameters.*

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/records/me` | `ANALYST, ADMIN` | Create a new financial record (Income/Expense) for the current user. |
| `POST` | `/api/v1/records/users/{userId}`| `ADMIN` | Create a financial record on behalf of a specific user. |
| `PUT` | `/api/v1/records/{id}` | `ADMIN` | Update details of an existing financial record. |
| `DELETE`|`/api/v1/records/{id}` | `ADMIN` | Soft-delete a financial record. |
| `GET` | `/api/v1/records/me` | `ANALYST, ADMIN` | Get current user's records. Supports optional filters: `?category=Salary` & `?type=INCOME`. |
| `GET` | `/api/v1/records/users/{userId}`| `ADMIN` | Get a specific user's records. Supports optional filters. |
| `GET` | `/api/v1/records/me/by-date` | `ANALYST, ADMIN` | Get current user's records within a strict date range: `?start=YYYY-MM-DD&end=YYYY-MM-DD`. |
| `GET` | `/api/v1/records/users/{userId}/by-date`| `ADMIN` | Get a specific user's records within a date range. |

---

## ⚙️ How to Run

### Option 1: Local Development (Using Gradle)
1. Ensure you have **Java 21** installed on your machine.
2. Clone the repository and navigate to the project directory.
3. Run the application using the Gradle wrapper:
   ```bash
   ./gradlew bootRun
   ```

### Option 2: Production-Ready (Using Docker)
This project includes a multi-stage Dockerfile optimized for production, keeping the final image lightweight and secure.
1. Ensure **Docker** is installed and running.
2. Build the Docker image:
   ```bash
   docker build -t finance-dashboard-api .
   ```
3. Run the application inside a container:
   ```bash
   docker run -p 8080:8080 finance-dashboard-api
   ```

---

**Accessing the Application:**
- **Base API URL:** `http://localhost:8080/api/v1`
- **H2 Database Console:** `http://localhost:8080/h2-console`
  *(JDBC URL: `jdbc:h2:mem:financedb` | User: `SA` | Password: `<leave blank>`)*


## 🏗 Notes for Evaluators
- **Architecture:** The application strictly follows a multi-tier architecture (Controller -> Service -> Repository), ensuring a clear separation of business logic and data access.
- **Security:** Endpoints are protected at the method level using `@PreAuthorize` alongside JWT stateless session management.
- **Defensive Programming:** Edge cases (like null values in projections) are handled gracefully (e.g., using `coalesce` in SQL and helper methods in services).
- **Testing:** The H2 console is enabled at `/h2-console` for quick database inspection during the evaluation.