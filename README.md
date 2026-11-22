# Telecom BSS Platform

Postpaid mobile operator backend — subscription lifecycle, billing, and customer management.

**Stack:** Java 21 · Spring Boot 3.2 · Maven · MySQL 8 · Flyway · Spring Security (JWT)

---

## Run locally

### Prerequisites
- Java 21
- MySQL 8 running locally with database `telecom_bss` and user `bss_user`

### 1. Create a local secrets file

```bash
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties
```

Open `application-local.properties` and fill in real values:

```properties
spring.datasource.password=your_db_password
jwt.secret=your_generated_secret
```

Generate a strong JWT secret:

```bash
openssl rand -base64 48
```

### 2. Start the application with the `local` profile

```bash
./mvnw spring-boot:run -Dspring.profiles.active=local
```

Or, instead of a properties file, export environment variables:

```bash
export DB_PASSWORD=your_db_password
export JWT_SECRET=$(openssl rand -base64 48)
./mvnw spring-boot:run
```

> **Note:** `application-local.properties` and `.env` are listed in `.gitignore` and must never be committed.

---

## Configuration reference

| Property / Env var | Default | Required in prod? |
|--------------------|---------|-------------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/telecom_bss?...` | Recommended |
| `DB_USERNAME` | `bss_user` | Recommended |
| `DB_PASSWORD` | *(none — must be set)* | **Yes** |
| `JWT_SECRET` | *(none — must be set)* | **Yes** |

---

## Running tests

```bash
./mvnw -B test
```

Tests that require a live database are skipped in the sandbox; set `DB_PASSWORD` and `JWT_SECRET` for a full run.
