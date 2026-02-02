
---
# GymCRM – Hibernate Task 
=============================

> **Important design decision:** Since the task is not role-based (no RBAC), **All actions except Create** require authentication and operate only **on the authenticated user’s own account.**. 
**Therefore, in operations described as “by username,” the username is derived from the **authenticated user via LoginRequest**, rather than being provided separately.

---

## 1) Infrastructure: DB, Hibernate, Flyway

* PostgreSQL connection is configured via environment variables defined in `application.properties`.
* **Hibernate DDL validation** is enabled: (hbm2ddl.auto=validate), which ensures the application fails at startup if entity mappings do not match the database schema.
* Flyway migrations are located under `classpath:db/migration`.
* `PersistenceConfiguration` centrally configures:

  * DataSource (HikariCP)
  * Flyway migration
  * EntityManagerFactory
  * TransactionManager

---

## 2) Domain Model and Relationships

### User – Trainee/Trainer (Parent-child / One-to-One)

* The task specifies a parent-child (one-to-one) relationship between Users and Trainee/Trainer.
* Instead of inheritance, `Trainee` and `Trainer` use **composition** with `User` via `@OneToOne (user_id)`.
* This approach provides better alignment with the database schema and avoids unnecessary complexity.

### Trainee – Trainer (Many-to-Many)

* The many-to-many relationship is modeled using a dedicated join entity:

  * `TraineeTrainer`
  * `TraineeTrainerId` (composite key)
* This design allows future extensibility and maintains normalization.

### Training – TrainingType

* `Training` references both `Trainee` and `Trainer` via foreign keys.
* `TrainingType` is treated as a fixed reference table and is not updated by the application.
* `TrainingTypeCode` is stored as a string enum using `@Enumerated(EnumType.STRING)`.

### N+1 Risk

* Critical relationships use `FetchType.LAZY` (especially `@ManyToOne`) to reduce unnecessary eager loading.

---

## 3) Layers and DRY Approach

### Why UserDomainService + UserService?

Shared logic between Trainee and Trainer required DRY principles:

* **UserDomainService**

  * Contains domain rules
  * Handles validation
  * Manages shared user creation logic
  * Log neccesary pıint
  

* **UserService**

  * Handles database operations
  * Manages authentication flow

### DTO Usage

* Record-based DTOs (Request/Response) are used to:

  * Prevent direct exposure of persistence entities outside the service layer
  * Reduce accidental modifications
  * Improve API clarity

---

## 4) Authentication Rule (Task Notes #2)

According to the task:

* **All operations except Create Trainee/Trainer must require authentication.**

Implementation:

* Authentication is performed via `LoginRequest(username, password)`.
* All service methods except Create accept a `LoginRequest`.
* “By username” operations use the authenticated user's username (self-only rule).
* Authentication logic is centralized in AuthService, and all authentication attempts are logged there.
---

## 5) Transaction Management

* Write operations → `@Transactional`
* Read operations → `@Transactional(readOnly = true)`
* This ensures both consistency and performance optimization.

---

# 6) Task Items – Implementation Mapping

Below is the mapping of each task item to its implementation:

### 1. Create Trainer Profile

* `TrainerService#createProfile(TrainerProfileRequest)`
* Credentials are generated via `CredentialsGenerator`, and user creation is handled by `UserService / UserDomainService`.

### 2. Create Trainee Profile

* `TraineeService#createProfile(TraineeProfileRequest)`
* Trainee-specific fields are populated from the request.

### 3. Trainee Username and Password Matching

* Authentication handled via `AuthService#authenticateAndGetUser`.

### 4. Trainer Username and Password Matching

* Same authentication process.`AuthService`.

### 5. Select Trainer Profile by Username

* `TrainerService#selectProfile(LoginRequest)`
* Username is derived from the authenticated user.

### 6. Select Trainee Profile by Username

* `TraineeService#selectProfile(LoginRequest)`

### 7. Trainee Password Change

* `TraineeService#changePassword(LoginRequest, newPassword)`

### 8. Trainer Password Change

* `TrainerService#changePassword(LoginRequest, newPassword)`

### 9. Update Trainer Profile

* `TrainerService#updateProfile(LoginRequest, TrainerProfileRequest)`

### 10. Update Trainee Profile

* `TraineeService#updateProfile(LoginRequest, TraineeProfileRequest)`

### 11. Activate/Deactivate Trainee

- Non-idempotent behavior is enforced centrally:
  - If the user is already **active** and an **activate** request is made → an exception is thrown.
  - If the user is **active** and a **deactivate** request is made → the state is changed to inactive.
  - If the user is already **inactive** and a **deactivate** request is made → an exception is thrown.
  - If the user is **inactive** and an **activate** request is made → the state is changed to active.
- The shared validation and state transition logic is centralized in the user-level service layer to avoid duplication.


### 12. Activate/Deactivate Trainer

* Same logic applies as Trainee. 

### 13. Delete Trainee Profile

* `TraineeService#deleteTrainee(LoginRequest)`
* Cascade delete removes related trainings.(Hard delete)

### 14. Get Trainee Trainings List

* `TrainingService#getTraineeTrainings(LoginRequest, TraineeTrainingQueryRequest)`
* Criteria API used for filtering.

### 15. Get Trainer Trainings List

* `TrainingService#getTrainerTrainings(LoginRequest, TrainerTrainingQueryRequest)`

### 16. Add Training

* `TrainingService#addTraining(LoginRequest, TrainingCreateRequest)`

### 17. Get Unassigned Trainers

* DAO query retrieves trainers not assigned to the trainee.

### 18. Update Trainee Trainers List

* Join table relationships updated dynamically.

---

# 7) Notes – Task Rules Covered

1. Credentials generation centralized
2. Authentication required for all non-create actions
   3–6. Required field validation implemented
3. Activate/Deactivate operations are non-idempotent
4. Training duration stored as Integer
5. Date fields use LocalDate
6. Proper FK relationships defined
7. Boolean active status managed centrally
8. TrainingType is treated as immutable reference data and is seeded via Flyway migrations. It is not updated through the application.
9. Each table has its own PK
10. Normalized Training and TrainingType tables
11. Proper transaction management
12. Hibernate DB configuration centralized
13. Unit tests and SLF4J logging implemented

---

## 8) Running the Application

1. Start PostgreSQL (Docker or local):

```bash
docker compose up -d
```

2. Configure environment variables:

3. Run the application:

```bash
mvn clean test
mvn spring-boot:run
```

Flyway runs migrations automatically, and Hibernate validates mappings.


___

## 9) Test Coverage

**Implementation:**

-   Unit tests are implemented for:
    -   Service layer

    -   Utility classes


**Coverage (by layer):**
-   `service`: 92%

-   `util`: 100%


![Coverage report](docs/images/coverage.png)

---

## 10) Development Summary

* Migrated the application from an in-memory architecture to a database-backed architecture using Hibernate/JPA.
* Implemented Flyway migration strategy
* Standardized exceptions
* Refined equals/hashCode
* Added unit tests and logging
