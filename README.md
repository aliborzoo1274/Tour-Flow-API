# Tour Flow API

Backend service for managing tours, travelers, and tour registrations. Tour Flow API is a stateless Spring Boot REST API backed by PostgreSQL, with JWT authentication and separate user/admin permissions.

## Features

- Traveler account creation and profile management
- Admin and traveler login with 24-hour JWT access tokens
- Public tour browsing
- Admin tour management, including cover/gallery image uploads
- Traveler tour registration with payment receipt uploads
- Registration and payment tracking
- Capacity management when travelers register or cancel
- Ownership checks for traveler and registration data
- Bean Validation and centralized API error handling
- CORS support for web clients

## Technology stack

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- JSON Web Tokens (JJWT 0.11.5)
- PostgreSQL
- Maven Wrapper

## Requirements

Install the following before starting the project:

- JDK 21 or newer
- PostgreSQL (PostgreSQL 14+ is recommended)
- Git

Verify Java and Maven Wrapper availability:

```bash
java -version
./mvnw -version
```

On Windows PowerShell, use `.\mvnw.cmd` instead of `./mvnw`.

## Local setup

### 1. Create the database

Create a PostgreSQL database named `tour_db`:

```sql
CREATE DATABASE tour_db;
```

The default local configuration expects:

| Setting | Default value |
| --- | --- |
| Database URL | `jdbc:postgresql://localhost:5432/tour_db` |
| Database user | `postgres` |
| Database password | `postgres` |

Change these values before running the API if your PostgreSQL installation uses different credentials. The application uses `spring.jpa.hibernate.ddl-auto=update`, so Hibernate creates and updates the application tables on startup. For production deployments, use an explicit migration strategy instead.

### 2. Configure the JWT secret

`JWT_SECRET_KEY` is required at startup and must be a sufficiently long secret for HS256 signing. Set it in the shell that will run the application:

PowerShell:

```powershell
$env:JWT_SECRET_KEY = "replace-with-a-long-random-secret"
```

Bash:

```bash
export JWT_SECRET_KEY="replace-with-a-long-random-secret"
```

Do not commit the secret to source control.

### 3. Start the application

From the repository root:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`.

To build and run the packaged application:

```bash
./mvnw clean package
java -jar target/tour-0.0.1-SNAPSHOT.jar
```

Uploaded files are stored in the local `uploads/` directory and are served through `/uploads/**`. Keep this directory persistent when deploying the API.

## Authentication

The API uses stateless Bearer tokens. Login endpoints are public; protected endpoints require:

```http
Authorization: Bearer <token>
```

There are two roles:

- `USER`: a registered traveler. Users can manage their own traveler record and registrations.
- `ADMIN`: an administrator. Admins can manage tours, view travelers, inspect tour registrations, and update payments.

### Create a traveler

Traveler creation is public. The national ID (`nid`) must contain exactly 10 digits and the phone number must contain exactly 11 digits.

```bash
curl -X POST http://localhost:8080/travelers \
  -H "Content-Type: application/json" \
  -d '{
    "nid": "1234567890",
    "name": "Emily",
    "surname": "Hart",
    "age": 29,
    "phoneNumber": "14155552671",
    "password": "change-this-password"
  }'
```

The response contains the traveler profile but never the stored password.

### Log in as a traveler

```bash
curl -X POST http://localhost:8080/auth/login/user \
  -H "Content-Type: application/json" \
  -d '{
    "nid": "1234567890",
    "password": "change-this-password"
  }'
```

### Log in as an admin

```bash
curl -X POST http://localhost:8080/auth/login/admin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin-password"
  }'
```

The project does not expose an admin-registration endpoint. An administrator must be provisioned in the `admins` table using a password encoded with the application's delegating password encoder before admin login can succeed. Manage administrator provisioning outside the public API.

## API reference

All IDs are numeric path parameters. Successful create operations return `201 Created`; successful deletes return `204 No Content`.

### Authentication and travelers

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/login/user` | Public | Log in with a traveler's 10-digit `nid` |
| `POST` | `/auth/login/admin` | Public | Log in with an admin username |
| `POST` | `/travelers` | Public | Create a traveler |
| `GET` | `/travelers` | Admin | List all travelers |
| `GET` | `/travelers/{id}` | User/Admin | Get a traveler; users may access their own record |
| `PUT` | `/travelers/{id}` | User/Admin | Update a traveler; users may update their own record |
| `DELETE` | `/travelers/{id}` | User/Admin | Delete a traveler; users may delete their own account |

### Tours

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/travels` | Public | List all tours |
| `GET` | `/travels/{id}` | Public | Get one tour |
| `POST` | `/travels` | Admin | Create a tour with `multipart/form-data` |
| `PUT` | `/travels/{id}` | Admin | Update a tour with `multipart/form-data` |
| `PATCH` | `/travels/{id}/registration-status?closed={true\|false}` | Admin | Open or close registrations |
| `DELETE` | `/travels/{id}` | Admin | Delete a tour |

Tour multipart fields:

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `name` | Text | Yes | Tour name |
| `capacity` | Integer | Yes | Positive total capacity |
| `cost` | Long | Yes | Non-negative tour cost |
| `startDate` | `YYYY-MM-DD` | Yes | Tour start date |
| `endDate` | `YYYY-MM-DD` | Yes | Tour end date |
| `boardingPlaces` | Repeated text field | No | Available boarding places |
| `description` | Text | No | Tour description |
| `coverImage` | File | No | Cover image |
| `images` | Repeated file field | No | Additional tour images |
| `existingImages` | Repeated text field | No | Existing image paths to retain during update |

Example admin tour creation:

```bash
curl -X POST http://localhost:8080/travels \
  -H "Authorization: Bearer <admin-token>" \
  -F "name=Highland Explorer" \
  -F "capacity=45" \
  -F "cost=2500" \
  -F "startDate=2026-07-10" \
  -F "endDate=2026-07-14" \
  -F "boardingPlaces=London" \
  -F "boardingPlaces=Manchester" \
  -F "description=Four days of guided travel" \
  -F "coverImage=@./images/highland-explorer.jpg"
```

### Registrations and payments

| Method | Path | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/travels/{travelId}/register` | User | Register the authenticated traveler for a tour |
| `GET` | `/travels/{travelId}/registrations` | Admin | List registrations for a tour |
| `GET` | `/travelers/{travelerId}/registrations` | User/Admin | List registrations for a traveler |
| `PUT` | `/registrations/{registrationId}` | User/Admin | Update registration details with `multipart/form-data` |
| `PUT` | `/registrations/{registrationId}/payment` | Admin | Update the amount paid |
| `DELETE` | `/registrations/{registrationId}` | User/Admin | Delete a registration |

Registration multipart fields:

| Field | Type | Required | Description |
| --- | --- | --- | --- |
| `boardingPlace` | Text | Yes | One of the tour's available boarding places |
| `receipt` | File | No | Payment receipt image/file |

Example registration:

```bash
curl -X POST http://localhost:8080/travels/1/register \
  -H "Authorization: Bearer <user-token>" \
  -F "boardingPlace=London" \
  -F "receipt=@./receipts/payment.jpg"
```

Example payment update:

```bash
curl -X PUT http://localhost:8080/registrations/1/payment \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{"amountPaid": 2500}'
```

## Error responses

Validation failures, missing resources, duplicate resources, authentication failures, and authorization failures are converted to HTTP error responses by the global exception handler. In general:

- `400 Bad Request`: invalid request data
- `401 Unauthorized`: missing or invalid JWT
- `403 Forbidden`: authenticated user lacks the required role or ownership
- `404 Not Found`: requested resource does not exist
- `409 Conflict`: duplicate resource or conflicting operation

## Configuration

The main configuration is in `src/main/resources/application.yaml`. The database and JWT settings can be overridden with Spring properties or environment variables. Common overrides include:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/tour_db"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "postgres"
$env:JWT_SECRET_KEY = "replace-with-a-long-random-secret"
```

File uploads are limited to 10 MB per file and 10 MB per request by default.

## Testing

Run the test suite with the Maven Wrapper:

```bash
./mvnw test
```

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

The current test suite includes an application context smoke test. Integration tests require a reachable PostgreSQL instance with configuration matching the test environment.

## Project structure

```text
src/
├── main/
│   ├── java/com/tour/tour/
│   │   ├── controller/    REST endpoints
│   │   ├── dto/           Request and response models
│   │   ├── exception/     API error types and handlers
│   │   ├── model/         JPA entities
│   │   ├── repository/    Spring Data repositories
│   │   ├── security/      JWT, CORS, and authorization
│   │   └── service/       Business logic
│   └── resources/
│       └── application.yaml
└── test/
    └── java/com/tour/tour/
```

## Development notes

- API sessions are stateless; the server does not maintain an HTTP session.
- JWTs expire 24 hours after issuance.
- Passwords are encoded before being stored.
- Public tour responses can include paths such as `/uploads/travels/...`; prepend the API origin when consuming them from a separate frontend.
- The default CORS configuration allows all origins. Restrict `allowedOriginPatterns` before deploying to production.
