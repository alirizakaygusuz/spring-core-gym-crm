# GymCRM - REST API

A gym customer relationship management system with JWT-based REST API authentication.

---

## Key Features

### Authentication & Authorization
- **JWT-based authentication** with Bearer token
- Login endpoint returns JWT token in response body
- Protected endpoints require `Authorization: Bearer <token>` header
- **Self-access enforcement:** Users can only access their own resources
  - Trainee `john.doe` can only view/update their own profile
  - Trainer `trainer.jane` can only view/update their own profile
- BCrypt password encryption
- Public endpoints: trainee/trainer registration, login, training types

### API Endpoints
- **Authentication:** Login (POST), change password
- **Trainee Management:** Register, get profile, update, delete, activate/deactivate
- **Trainer Management:** Register, get profile, update, activate/deactivate
- **Training Management:** Add training (trainer only), get training types
- **Relationships:** Assign/update trainee's trainers, get unassigned trainers

### Important Rules
- **Only trainers can create trainings** - Trainees cannot add training sessions
- Training creation requires trainer authentication (JWT token must belong to a trainer)

---

## Implementation Details

### Architecture
- **Context-based authentication:** Custom `AuthenticationContext` handles authentication/authorization via `ContextLoaderListener`
- **Removed dispatcher servlet overhead:** Authentication now handled at context level via `RequestContextHolder`
- **Clean controller layer:** Controllers no longer require `HttpServletRequest` parameters; authentication handled transparently

### Error Handling
- Enhanced `ApiError` structure with URN, request ID, and timestamp for better traceability
- Request ID extracted via `RequestContextHolder` instead of directly from `HttpServletRequest`
- Distinct exception types:
  - `AuthenticationFailedException`: Invalid credentials
  - `AuthorizationFailedException`: Forbidden access (403)

### Code Quality
- Service methods refactored: self-access control moved to context layer
- Public service methods organized with related private methods grouped below
- Type inference used where appropriate (`var` instead of explicit types)
- Generic `ValidationUtils` class replaces specific validator classes
- Validation methods throw exceptions explicitly on failure
- Swagger schemas aligned with mock data for easier manual testing

---

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Step 1: Build
```bash
mvn clean package -Dmaven.test.skip=true
```

### Step 2: Start Containers
```bash
docker-compose up -d --build
```

### Step 3: Check Logs
```bash
docker logs -f ali-gymcrm-tomcat
```

Wait for: `Server startup in [XXXX] milliseconds`

### Step 4: Access API Documentation
**Swagger UI:** http://localhost:8080/swagger-ui/index.html

---

## How to Test

### Test Users (Pre-loaded)

| Username      | Password     | Role    |
|---------------|--------------|---------|
| john.doe      | password123  | Trainee |
| trainer.jane  | password123  | Trainer |

### Testing with Swagger UI

#### 1. Open Swagger UI
Navigate to: http://localhost:8080/swagger-ui/index.html

![Swagger UI](docs/images/swagger_ui.png)

#### 2. Login to Get JWT Token
```
POST /api/v1/login
Body: {
  "username": "john.doe",
  "password": "password123"
}
→ 200 OK
Response: {
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

![Login](docs/images/login_trainee.png)

#### 3. Authorize with JWT Token
- Click **"Authorize"** button (top right)
- Enter: `Bearer <your-token-here>`
- Click **"Authorize"**
- Click **"Close"**

#### 4. Test Examples

**Example 1: Get Trainee Profile (requires auth)**
```
GET /api/v1/trainees/john.doe
Authorization: Bearer <token>
→ 200 OK
```

**Example 2: Register New Trainee (public - no auth)**
```
POST /api/v1/trainees
Body: {
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St"
}
→ 200 OK (returns username & password)
```

![Register Trainee](docs/images/register_trainee.png)
![Register Trainee DB](docs/images/register_trainee_db.png)

**Example 3: Get Training Types (public - no auth)**
```
GET /api/v1/trainings/types
→ 200 OK (CARDIO, STRENGTH, FLEXIBILITY, etc.)
```

**Example 4: Get Trainer Profile (requires auth)**
```
Authorize with trainer.jane's JWT token
GET /api/v1/trainers/trainer.jane
→ 200 OK
```

**Example 5: Update Trainee Profile**
```
Authorize with john.doe's JWT token
PUT /api/v1/trainees/john.doe
Body: {
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "address": "New Address 456",
  "isActive": true
}
→ 200 OK
```

**Example 6: Change Password**
```
Authorize with john.doe's JWT token
PATCH /api/v1/change-password
Body: {
  "username": "john.doe",
  "oldPassword": "password123",
  "newPassword": "newPassword456"
}
→ 200 OK
```

**Example 7: Add Training**
```
Authorize with trainer.jane's JWT token
POST /api/v1/trainings
Body: {
  "traineeUsername": "john.doe",
  "trainerUsername": "trainer.jane",
  "trainingName": "Morning Cardio",
  "trainingDate": "2024-06-15",
  "trainingDuration": 60
}
→ 200 OK
```

---

## Self-Access Enforcement

**What it means:**
- Each user can only access their own resources
- Attempting to access another user's profile returns `403 Forbidden`
- Authorization is now handled by `AuthenticationContext` at the context layer

**Examples:**

✅ **Allowed:**
```bash
# john.doe accessing own profile
Authorization: Bearer <john.doe's-token>
GET /api/v1/trainees/john.doe
→ 200 OK
```

❌ **Forbidden:**
```bash
# john.doe trying to access trainer.jane's profile
Authorization: Bearer <john.doe's-token>
GET /api/v1/trainers/trainer.jane
→ 403 Forbidden (AuthorizationFailedException)
```

---

## Enhanced Error Responses

All error responses include:
- **URN:** Unique resource identifier for the error type
- **Request ID:** Correlation ID for tracking across logs
- **Timestamp:** ISO-8601 formatted error occurrence time

**Example:**
```json
{
  "urn": "urn:gymcrm:error:authorization-failed",
  "requestId": "a7f3c2e1-4b9d-8e2f-1a3c-5d6e7f8g9h0i",
  "timestamp": "2024-06-15T14:32:15.234Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied"
}
```

---

## Test Coverage

Unit tests cover:
- **Service layer:** 88% coverage
- **Utility classes:** 100% coverage
- **Validators:** 93% coverage
- **Auth layer:** 100% coverage

![Test Coverage](docs/images/test_coverage.png)

### Running Tests
```bash
mvn test
```

All tests updated to reflect:
- JWT-based authentication flow
- Context-layer authorization
- Enhanced error response structure

---

## API Documentation

Full API documentation is available at:
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

All endpoints, request/response schemas, JWT authentication requirements, and example payloads are documented there.

**Note:** This project uses **Springdoc OpenAPI 3** with Bearer token authentication configured for JWT. Swagger UI is pre-configured with mock data examples that match the database seed data for easier manual testing.

---

## Database

![Register User DB](docs/images/register_user_db.png)

Pre-loaded test users are stored with BCrypt-hashed passwords. Upon registration, unique usernames are generated in `FirstName.LastName` format with automatic numeric suffixes for duplicates.