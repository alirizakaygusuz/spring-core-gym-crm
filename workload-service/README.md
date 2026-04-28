# **Workload Service**

A microservice responsible for tracking and calculating trainer monthly training workload summaries. Communicates with `gym-crm` asynchronously via **ActiveMQ Artemis** using Spring JMS.

---

## **Overview**

Every time a training session is added or deleted in `gym-crm`, a `TrainerWorkloadRequest` event is published to `workload.queue`. This service consumes those events and maintains a per-trainer monthly summary of total training durations in **MongoDB**.

---

## **Architecture**

```
gym-crm (Producer)
    ↓ workload.queue
ActiveMQ Artemis
    ↓
TrainerWorkloadMessageConsumer (@JmsListener)
    ↓ JmsMessageValidator (Jakarta Bean Validation)
TrainerWorkloadService
    ↓ processTrainerWorkload(ActionType: ADD | DELETE)
MongoDB (trainer_workload_summary collection)
```

---

## **Key Features**

### **Messaging**
- `TrainerWorkloadMessageConsumer` listens on `workload.queue` via `@JmsListener`
- Single consumer handles both `ADD` and `DELETE` action types via `ActionType` enum
- **`JmsMessageValidator`** validates incoming messages using Jakarta Bean Validation annotations — invalid messages trigger exception → retry → DLQ
- **`TrainerWorkloadDeadLetterHandler`** listens on `DLQ` — logs failed messages at ERROR level with transaction ID
- **MDC transaction ID propagation** — `transactionId` carried via JMS message property, set into MDC for cross-service log tracing with `gym-crm`
- **Horizontal scaling:** 1–5 consumer threads via `setConcurrency("1-5")`
- **JSON serialization** via `JacksonJsonMessageConverter` — no raw JMS primitives

### **Workload Calculation**

`processTrainerWorkload(TrainerWorkloadRequest)` dispatches by `ActionType`:

**ADD:**
1. Find trainer record by username or create new one
2. Find yearly summary for training date's year or create new one
3. Find monthly summary for training date's month or create new one
4. Increment `totalTrainingDuration` by `trainingDuration`

**DELETE:**
1. Find trainer record — throws `TrainerWorkloadNotFoundException` if not found
2. Find yearly/monthly summary — throws if not found
3. Decrement `totalTrainingDuration`
    - Result < 0 → throws `InsufficientTrainerWorkloadDurationException`
    - Result == 0 → removes monthly entry; if year becomes empty → removes yearly entry
    - Result > 0 → updates duration

**Data Model (MongoDB, embedded document):**

```
TrainerWorkloadSummary (@Document)
├── username, firstName, lastName, isActive
└── List<TrainerWorkloadYearlySummary>
    └── year
        └── List<TrainerWorkloadMonthlySummary>
            └── month, totalTrainingDuration
```

**Indexes:**
- Compound index on `firstName` + `lastName` for name-based search

**Request Contract (`TrainerWorkloadRequest`):**

| Field | Type | Validation |
|-------|------|-----------|
| `username` | String | `@NotBlank` |
| `firstName` | String | `@NotBlank` |
| `lastName` | String | `@NotBlank` |
| `isActive` | Boolean | `@NotNull` |
| `trainingDate` | LocalDate | `@NotNull` |
| `trainingDuration` | Integer | `@NotNull`, `@Positive` |
| `actionType` | ActionType | `@NotNull` (ADD / DELETE) |

### **Profile-Based Configuration**
Both broker URL and MongoDB connection externalized via `.env` per environment.

Profiles: `local`, `dev`, `docker`, `stg`, `prod`

---

## **Running the Application**

### **Build & Start**

#### **Step 1: Build image**
```bash
docker build -t ali-gymcrm-workload:latest .
```

#### **Step 2: Run standalone (disabled integrations)**
```bash
docker run --rm -p 8082:8082 ali-gymcrm-workload:latest
```

#### **Step 3: Run with full stack (enabled integrations)**
# With logs invisible
```bash
docker compose up -d
```

# With logs visible
```bash
docker compose up --build
```

Wait for: `Started WorkloadServiceApplication in X seconds`

---

## **Security**

All API endpoints are secured with **JWT Bearer token** authentication. The token is issued by `gym-crm` and validated here using the same shared secret.

- `JwtAuthenticationFilter` extracts and validates the token from the `Authorization` header
- `JwtService` validates signature, expiration, and extracts username and roles
- Stateless session — no server-side session storage
- `X-Transaction-Id` header is read (or generated) on every request and added to MDC

**Public endpoints (no auth required):**
- `/swagger-ui/**`, `/v3/api-docs/**`
- `/actuator/**`

---

## **API Endpoints**

All endpoints require `Authorization: Bearer <token>` header.
Token is obtained from `gym-crm` via `POST /api/v1/login`.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/workload/trainers` | Process trainer workload (ADD/DELETE) |
| `GET` | `/api/v1/workload/trainers/{username}/summary` | Get trainer monthly workload summary |

### **Testing with Swagger UI**

1. Navigate to `http://localhost:8082/swagger-ui/index.html`
2. Login to `gym-crm` (`POST /api/v1/login`) to get a JWT token
3. Click **Authorize** → enter `Bearer <token>`
4. Call `GET /api/v1/workload/trainers/{username}/summary?year=2024&month=6`


### **Code Quality**
- Service layer fully covered with unit tests (80% coverage)
- Swagger schemas aligned with seed data for easier manual testing

## Testing

### Overview

The project uses **Cucumber BDD** for component testing with Testcontainers, and provides infrastructure support for gym-crm's cross-service integration tests.

```
src/test/java/.../workload_service/
├── component/
│   ├── steps/
│   │   ├── TrainerWorkloadSteps.java
│   │   └── SharedState.java
│   ├── support/
│   │   └── JwtFactoryTest.java
│   ├── CucumberRunner.java
│   └── CucumberSpringConfiguration.java
│
src/test/resources/
└── features/
    └── trainer-workload.feature
│
src/main/java/.../controller/test/
└── TestResetController.java              # @Profile("integration") — MongoDB cleanup for cross-service tests
```

### Component Tests

Run against an isolated Spring Boot context using **Testcontainers** (MongoDB, Artemis). No external services required. Uses `@ActiveProfiles("test")`.

**Scenarios covered:**

- ADD workload processing (happy path)
- DELETE workload processing (happy path)
- Validation failure on missing required fields (400)
- Unauthenticated workload processing (401)
- Workload summary retrieval (happy path)
- Unauthenticated summary retrieval (401)
- Summary retrieval with invalid parameters (400)

**Test infrastructure:**

- `JwtFactoryTest` generates valid JWT tokens for authenticated test requests using the same signing key as the application
- `SharedState` (`@ScenarioScope`) carries response and token across steps within a single scenario
- `@Before` hook clears MongoDB before each scenario via `summaryRepository.deleteAll()`

```bash
mvn test
```

### Integration Test Support

workload-service participates in gym-crm's Cucumber integration tests, which verify JMS-based async communication between the two services.

A `TestResetController` (`@Profile("integration")`) exposes `DELETE /api/v1/test/reset` to drop the `trainer_workload_summary` MongoDB collection. This endpoint is called by gym-crm's `DatabaseCleanupHook` before each integration scenario to ensure test isolation. The endpoint is permitted without authentication in `SecurityConfig` since the profile guard already prevents production exposure.

**Running workload-service for integration tests:**

```bash
# Ensure both profiles are active in docker-compose.yml:
# SPRING_PROFILES_ACTIVE: docker,integration

docker compose -f compose.yml -f compose.integration.yml up -d
```

The integration test scenarios themselves are defined and executed from the gym-crm module.


### **GET Summary — Example**

```http
GET /api/v1/workload/trainers/trainer.jane/summary?year=2024&month=6
Authorization: Bearer <token>
```

```json
{
  "data": {
    "username": "trainer.jane",
    "firstName": "Jane",
    "lastName": "Smith",
    "isActive": true,
    "yearlySummaries": [
      {
        "year": 2024,
        "monthlySummaries": [
          {
            "month": 6,
            "totalTrainingDuration": 120
          }
        ]
      }
    ]
  }
}
```

### **Error Response Format**

```json
{
  "error": {
    "requestId": "123e4567-e89b-12d3-a456-426614174000",
    "urn": "urn:com.alirizakaygusuz.gymcrm.workload_service:api:GET:v1:workload",
    "timestamp": "2024-06-01T12:00:00Z",
    "code": "WORKLOAD_NOT_FOUND",
    "message": "Trainer with username trainer.jane not found",
    "fieldErrors": null
  }
}
```

---

## **Monitoring & Observability**

### **Actuator**
- **Health:** `http://localhost:8082/actuator/health`
- **Liveness:** `http://localhost:8082/actuator/health/liveness`
- **Readiness:** `http://localhost:8082/actuator/health/readiness`
- **Info:** `http://localhost:8082/actuator/info`
- **Prometheus:** `http://localhost:8082/actuator/prometheus`

### **Custom Prometheus Metrics**

```prometheus
trainer_workload_add_attempts_total
trainer_workload_add_total
trainer_workload_delete_attempts_total
trainer_workload_delete_total
trainer_workload_get_summary_attempts_total
trainer_workload_get_summary_total
```

### **Transaction ID Tracing**
Every log entry includes `[transactionId=...]` propagated from `gym-crm` via JMS message property or HTTP `X-Transaction-Id` header. Allows end-to-end request tracing across both services.

---

## **Test Coverage**

```bash
mvn test
```

Service layer: **98% line coverage**

---