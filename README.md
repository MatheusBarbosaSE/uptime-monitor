# 🚀 Uptime Monitor (Back-end)

![Java](https://img.shields.io/badge/Java-21%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Database](https://img.shields.io/badge/Database-PostgreSQL-blueviolet)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

A complete, multi-tenant REST API built with **Java** and **Spring Boot** to monitor website uptime. This back-end service allows users to register, manage their own "targets" (websites), and receive **email alerts** when a site goes down.

It features a dynamic, persistent job scheduling system (one per target) and is secured using **JWT authentication**.

This project is the back-end service for the [Uptime Monitor UI (React) frontend](https://github.com/MatheusBarbosaSE/uptime-monitor-ui).

---

## 📑 Table of Contents

- [Features](#-features)
- [Installation](#-installation)
- [API Endpoints (Usage)](#-api-endpoints-usage)
- [Technologies Used](#-technologies-used)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- ✅ **JWT Authentication:** Secure `register` and `login` endpoints.
- ✅ **Multi-Tenant API:** Users can only access, update, or delete their *own* targets.
- ✅ **Dynamic Task Scheduling:** Each target is monitored on its *own* custom interval (e.g., 1 min, 5 min, 10 min).
- ✅ **Email Alerts:** Automatically sends email alerts (via SMTP/Mailtrap) when a target's status changes from `ONLINE` to `OFFLINE`.
- ✅ **History Tracking:** Records every single check in the database.
- ✅ **Paginated API:** The history endpoint (`/health-checks`) includes pagination and date-range filtering.
- ✅ **Automated Integration Tests:** Uses **Testcontainers** to prove security and logic against a real PostgreSQL Docker container.
- ✅ **Secure Configuration:** No hardcoded secrets. All keys and passwords are loaded from external files (`.env` and `.properties`).
- ✅ **Database:** Comes with a `docker-compose.yml` for instant PostgreSQL setup.

---

## ⚙️ Installation

### Requirements
- [Java JDK 21+](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/) (and Docker Compose)

### 1. Clone the Repository
```bash
git clone [https://github.com/MatheusBarbosaSE/uptime-monitor.git](https://github.com/MatheusBarbosaSE/uptime-monitor.git)
cd uptime-monitor
```

### 2. Configure Environment Variables (Secrets)

This project requires **two** environment files to run. **Do not** commit these files.

**A) Docker Secrets (`.env`):**
This file configures the PostgreSQL container *before* it starts.

```bash
# Create the .env file from the example
cp .env.example .env
```
Now, **open the new `.env` file** and set your `POSTGRES_PASSWORD_CHANGE_ME`.

**B) Spring Application Secrets (`application-dev.properties`):**
This file configures the Java application (database connection, JWT key, email server).

```bash
# Navigate to the resources folder
cd src/main/resources

# Create the dev properties file
cp application-dev.properties.example application-dev.properties
```
Now, **open `application-dev.properties`** and fill in all required secrets:
- `spring.datasource.password`: **(Must match the password you set in `.env`)**
- `application.security.jwt.secret-key`: (Generate a new 512-bit Base64 key)
- `spring.mail.username` & `spring.mail.password`: (Get from your Mailtrap.io Sandbox)

### 3. Start the Database & Application

1.  **Start the Database (from the root folder):**
    ```bash
    docker-compose up -d
    ```
2.  **Run the Application (from the root folder):**
    You can run the app from your IDE (like IntelliJ) or by using Maven:
    ```bash
    mvn spring-boot:run
    ```
The API will be running on `http://localhost:8080`.

---

## 💻 API Endpoints (Usage)

A tool like [**Postman**](https://www.postman.com/downloads/) or [Insomnia](https://insomnia.rest/download) is highly recommended for testing the API endpoints.

All endpoints (except `/api/auth/**`) are **protected**. You must send a `Bearer Token` in the `Authorization` header.

### Authentication

**`POST /api/auth/register`**
Create a new user.
```json
{
  "username": "user",
  "password": "your-password",
  "email": "user@example.com"
}
```

**`POST /api/auth/login`**
Get your JWT token.
```json
{
  "username": "user",
  "password": "your-password"
}
```

### User Profile (Protected)

**`GET /api/user/me`**
Get your current user details (username, email).

**`PUT /api/user/profile`**
Update your username or email.
```json
{
  "username": "new-username",
  "email": "new-email@example.com"
}
```

**`PUT /api/user/password`**
Change your password.
```json
{
  "oldPassword": "your-password",
  "newPassword": "new-strong-password"
}
```

### Targets (CRUD - Protected)

**`POST /api/targets`**
Create a new target to monitor.
```json
{
  "name": "My Blog",
  "url": "[https://my-blog.com](https://my-blog.com)",
  "checkInterval": 5
}
```

**`GET /api/targets`**
Get a list of *your* targets.

**`PUT /api/targets/{id}`**
Update a target.

**`DELETE /api/targets/{id}`**
Delete a target and all its history.

### History & Reports (Protected)

**`GET /api/targets/{targetId}/health-checks`**
Get the paginated history for one of your targets.

**Query Parameters:**
- `page` (optional): The page number (default: `0`).
- `size` (optional): The page size (default: `10`).
- `sort` (optional): The sort order (default: `checkedAt,desc`).
- `startDate` (optional): ISO 8601 timestamp (e.g., `2025-11-01T00:00:00Z`).
- `endDate` (optional): ISO 8601 timestamp.

If no `startDate` or `endDate` is provided, the API defaults to the **last 24 hours**.

---

## 🛠 Technologies Used

- **Backend:**
    - [Java 21](https://www.oracle.com/java/technologies/downloads/)
    - [Spring Boot 3](https://spring.io/projects/spring-boot)
    - [Spring Security (JWT)](https://spring.io/projects/spring-security)
    - [Spring Data JPA (Hibernate)](https://spring.io/projects/spring-data-jpa)
    - [Spring Mail](https://spring.io/projects/spring-framework/mail)
- **Database:**
    - [PostgreSQL](https://www.postgresql.org/)
    - [Docker](https://www.docker.com/)
- **Testing:**
    - [JUnit 5](https://junit.org/junit5/)
    - [Testcontainers](https://www.testcontainers.org/) (for true integration tests)
    - [MockMvc](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#spring-mvc-test-framework)
- **Tooling:**
    - [Maven](https://maven.apache.org/)
    - [Mailtrap.io](https://mailtrap.io/) (for email testing)
- **Security:**
    - [jjwt](https://github.com/jwtk/jjwt)

---

## 📂 Project Structure

```bash
uptime-monitor/
│
├── .mvn/                 # Maven wrapper files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── matheusbarbosase/
│   │   │           └── uptime_monitor/
│   │   │               ├── auth/         # Auth DTOs, Service, Controller & JWT logic
│   │   │               ├── config/       # Spring Security, App Config (Beans)
│   │   │               ├── controller/   # API controllers (Target, HealthCheck)
│   │   │               ├── dto/          # Data Transfer Objects
│   │   │               ├── exception/    # Custom exceptions (ResourceNotFound)
│   │   │               ├── model/        # JPA Entities (User, Target, HealthCheck)
│   │   │               ├── repository/   # Spring Data JPA Repositories
│   │   │               ├── service/      # Business logic (Monitoring, Email, Tasks)
│   │   │               ├── user/         # User profile management (Service, DTOs, Controller)
│   │   │               └── UptimeMonitorApplication.java # Main entry point
│   │   └── resources/
│   │       ├── application.properties  # Main config
│   │       └── application-dev.properties.example # Example secrets
│   └── test/
│       └── java/
│           └── com/
│               └── matheusbarbosase/
│                   └── uptime_monitor/
│                       ├── config/       # Testcontainers base configuration
│                       └── controller/   # Integration tests for controllers
│
├── .gitignore            # Ignore rules for Git
├── .env.example          # Example secrets for Docker
├── docker-compose.yml    # PostgreSQL database service
├── LICENSE               # MIT License
├── mvnw                  # Maven wrapper (Linux/Mac)
├── mvnw.cmd              # Maven wrapper (Windows)
├── pom.xml               # Maven dependencies
└── README.md             # Project documentation
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'feat: add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

Feel free to open **issues** for bug reports or suggestions.

---

## 📄 License

This project is licensed under the **[MIT License](LICENSE)**.  
You are free to use, copy, modify, and distribute this software, provided you keep the original credits.