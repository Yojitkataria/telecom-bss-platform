# Telecom BSS - Setup & Testing Guide

Comprehensive manual for environment configuration, database setup, build instructions, and sequential end-to-end API testing for the Telecom Business Support System (BSS).

---

## 1. Prerequisites & Environment Setup

### System Requirements
- **Java**: JDK 21 (LTS)
- **Framework**: Spring Boot 3.2.0 (managed via Maven wrapper `./mvnw` / `mvnw.cmd`)
- **Database**: MySQL 8.0+
- **API Client**: Postman, Insomnia, or cURL

---

## 2. Database Configuration

### 2.1 MySQL Database Creation
Connect to your local MySQL instance (CLI or MySQL Workbench) and create the database schema:

```sql
CREATE DATABASE telecom_bss CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Optional: Create a dedicated user for BSS
CREATE USER IF NOT EXISTS 'bss_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON telecom_bss.* TO 'bss_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2.2 Application Configuration (`application.properties`)
The application is configured in `src/main/resources/application.properties`:

```properties
spring.application.name=telecom-bss
server.port=8080

# Datasource configuration
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/telecom_bss?useSSL=false&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USERNAME:bss_user}
spring.datasource.password=${DB_PASSWORD}

# JPA & Hibernate validation
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false
spring.jpa.show-sql=false

# Flyway database migrations
spring.flyway.enabled=true

# JWT security settings
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=3600000
```

### 2.3 Environment Variables
Before running the application, provide the required credentials as environment variables:

#### Windows (PowerShell)
```powershell
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="c2VjdXJlX3JhbmRvbV9iYXNlNjRfa2V5X2Zvcmp3dF9zaGEyNTZfc2VjcmV0X21pbmltdW1fMjU2Yml0cw=="
```

#### Windows (Command Prompt)
```cmd
set DB_PASSWORD=your_password
set JWT_SECRET=c2VjdXJlX3JhbmRvbV9iYXNlNjRfa2V5X2Zvcmp3dF9zaGEyNTZfc2VjcmV0X21pbmltdW1fMjU2Yml0cw==
```

#### Linux / macOS (Bash / Zsh)
```bash
export DB_PASSWORD="your_password"
export JWT_SECRET="c2VjdXJlX3JhbmRvbV9iYXNlNjRfa2V5X2Zvcmp3dF9zaGEyNTZfc2VjcmV0X21pbmltdW1fMjU2Yml0cw=="
```

> **Note on Flyway**: On application startup, Flyway automatically applies migrations:
> - `V1__init_schema.sql`: Creates all core tables (`users`, `customers`, `sim_cards`, `plans`, `addons`, `subscriptions`, `invoices`, etc.).
> - `V2__seed_data.sql`: Seeds initial test catalog plans, dummy users, sample customers, and pre-allocated SIM cards.

---

## 3. Running the Application

Execute the Maven wrapper from the project root:

```powershell
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Once started:
- **Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 4. Sequential Postman API Execution Order

Follow this exact sequential workflow to test the end-to-end subscriber journey:

```
[Step 1: Register & Login] -> [Step 2: Customer Onboarding (KYC)] -> [Step 3: Provision SIM] -> [Step 4: Activate Subscription] -> [Step 5: Verify Active Subscriptions]
```

---

### Step 1: Authentication (Registration & Login)

#### 1A. User Registration
Creates a new customer account in the system.

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/auth/register`
- **Headers**:
  - `Content-Type: application/json`
- **Request Body**:
```json
{
  "email": "test.subscriber@telco.com",
  "password": "Password123!"
}
```
- **Expected Response (`201 Created`)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LnN1YnNjcmliZXJAdGVsY28uY29tIiwiaWF0IjoxNzAw...",
  "email": "test.subscriber@telco.com",
  "role": "CUSTOMER"
}
```

#### 1B. User Login
Authenticates an existing account and generates a fresh JWT bearer token.

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/auth/login`
- **Headers**:
  - `Content-Type: application/json`
- **Request Body**:
```json
{
  "email": "test.subscriber@telco.com",
  "password": "Password123!"
}
```
- **Expected Response (`200 OK`)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0LnN1YnNjcmliZXJAdGVsY28uY29tIiwiaWF0IjoxNzAw...",
  "email": "test.subscriber@telco.com",
  "role": "CUSTOMER"
}
```

> **Important**: Copy the `token` value. For all subsequent requests in Steps 2 to 5, add this token in the Postman **Authorization** tab as **Bearer Token**, or add the header:
> `Authorization: Bearer <your_jwt_token>`

---

### Step 2: Customer Onboarding (KYC Profile)
Binds personal details and identity verification documents to the authenticated user.

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/customers/me`
- **Headers**:
  - `Authorization: Bearer <your_jwt_token>`
  - `Content-Type: application/json`
- **Request Body**:
```json
{
  "fullName": "Aarav Sharma",
  "dateOfBirth": "1995-04-18",
  "address": "Flat 302, Green Glen Layout, Bellandur",
  "city": "Bengaluru",
  "kycDocumentType": "AADHAAR",
  "kycDocumentNo": "4123-5678-9012"
}
```
- **Expected Response (`201 Created`)**:
```json
{
  "id": 5,
  "email": "test.subscriber@telco.com",
  "fullName": "Aarav Sharma",
  "dateOfBirth": "1995-04-18",
  "address": "Flat 302, Green Glen Layout, Bellandur",
  "city": "Bengaluru",
  "kycStatus": "PENDING",
  "kycDocumentType": "AADHAAR",
  "kycDocumentNo": "4123-5678-9012"
}
```

*(Optional verification)*:
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/customers/me`
- **Headers**: `Authorization: Bearer <your_jwt_token>`
- Returns the customer profile created above.

---

### Step 3: SIM Inventory Provisioning
Adds a new physical/eSIM into the inventory pool with status `AVAILABLE`.

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/sims`
- **Headers**:
  - `Authorization: Bearer <your_jwt_token>`
  - `Content-Type: application/json`
- **Request Body**:
```json
{
  "msisdn": "9876500099",
  "iccid": "8991000000000000099"
}
```
- **Expected Response (`201 Created`)**:
```json
{
  "id": 7,
  "msisdn": "9876500099",
  "iccid": "8991000000000000099",
  "status": "AVAILABLE"
}
```

*(Optional inventory check)*:
- **Method**: `GET`
- **URL**: `http://localhost:8080/api/sims`
- **Headers**: `Authorization: Bearer <your_jwt_token>`
- Lists all warehouse SIMs and their current statuses. Note the `id` of the available SIM (e.g., `7`).

---

### Step 4: Subscription Activation
Executes atomic provisioning linking the customer, plan, and SIM card. Transitions SIM status to `ASSIGNED` and activates the subscription.

*(Prerequisite: check available plans via `GET /api/plans` — seed data includes Plan ID `1` "Basic 299", Plan ID `2` "Standard 499", Plan ID `3` "Premium 999")*

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/subscriptions`
- **Headers**:
  - `Authorization: Bearer <your_jwt_token>`
  - `Content-Type: application/json`
- **Request Body**:
```json
{
  "planId": 1,
  "simId": 7
}
```
- **Expected Response (`201 Created`)**:
```json
{
  "id": 5,
  "customerId": 5,
  "customerName": "Aarav Sharma",
  "planName": "Basic 299",
  "msisdn": "9876500099",
  "iccid": "8991000000000000099",
  "status": "ACTIVE",
  "billDay": 23,
  "activatedAt": "2026-11-23T18:05:00",
  "createdAt": null
}
```

> **Under the Hood**:
> - SIM Card `7` status automatically transitions from `AVAILABLE` to `ASSIGNED`.
> - If you attempt to subscribe again using SIM `7`, the API returns `400 Bad Request` ("SIM card is not available for subscription").

---

### Step 5: Verify Active Subscriptions
Retrieves all subscriptions tied to the currently authenticated customer.

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/subscriptions/me`
- **Headers**:
  - `Authorization: Bearer <your_jwt_token>`
- **Expected Response (`200 OK`)**:
```json
[
  {
    "id": 5,
    "customerId": 5,
    "customerName": "Aarav Sharma",
    "planName": "Basic 299",
    "msisdn": "9876500099",
    "iccid": "8991000000000000099",
    "status": "ACTIVE",
    "billDay": 23,
    "activatedAt": "2026-11-23T18:05:00",
    "createdAt": "2026-11-23T18:05:00"
  }
]
```

---

## 5. Known Schema Mapping Notes

When working with Spring Data JPA and strict Hibernate schema validation (`spring.jpa.hibernate.ddl-auto=validate`), note the following field mappings:

1. **`User.role`**:
   - Database column: `role ENUM('ADMIN', 'CSR', 'CUSTOMER') NOT NULL`
   - Entity mapping:
     ```java
     @Enumerated(EnumType.STRING)
     @Column(name = "role", length = 20)
     private Role role;
     ```

2. **`Customer.kyc_status`**:
   - Database column: `kyc_status ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING'`
   - Entity mapping:
     ```java
     @Enumerated(EnumType.STRING)
     @Column(name = "kyc_status", length = 20)
     private KycStatus kycStatus;
     ```

3. **`Subscription.bill_day`**:
   - Database column: `bill_day TINYINT NOT NULL`
   - Entity mapping:
     ```java
     @Column(name = "bill_day", nullable = false, columnDefinition = "TINYINT")
     private Integer billDay;
     ```
   - **Why this is critical**: In standard Java, `Integer` maps by default to standard SQL `INTEGER`/`INT` (4 bytes). When Flyway creates the table with `TINYINT` (1 byte, day range 1-28), Hibernate's schema validator halts application startup with:
     ```text
     SchemaManagementException: Schema-validation: wrong column type encountered in column [bill_day] in table [subscriptions]; found [tinyint (Types#TINYINT)], but expecting [integer (Types#INTEGER)]
     ```
     Adding `columnDefinition = "TINYINT"` explicitly instructs Hibernate that the field matches the database's `TINYINT` definition.

4. **`SimCard.status`**:
   - Database column: `status ENUM('AVAILABLE', 'ASSIGNED', 'BLOCKED') NOT NULL DEFAULT 'AVAILABLE'`
   - Entity mapping:
     ```java
     @Column(name = "status", columnDefinition = "ENUM('AVAILABLE', 'ASSIGNED', 'BLOCKED')")
     private String status;
     ```

5. **`Subscription.status`**:
   - Database column: `status ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'TERMINATED') NOT NULL DEFAULT 'PENDING'`
   - Entity mapping:
     ```java
     @Column(name = "status", columnDefinition = "ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'TERMINATED')")
     private String status;
     ```
