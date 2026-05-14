# Spring Gym CRM

A production-style Spring Boot microservice system for gym management 
and trainer workload tracking. Built incrementally through mentor-reviewed 
milestones as part of EPAM Systems' Java Backend Specialization Program 
— covering REST API design, async messaging, security, and multi-layer 
testing.

---

## Services

- [`gym-crm`](./gym-crm) — main backend service for trainee/trainer 
  management, training management, authentication, and authorization.
- [`workload-service`](./workload-service) — async microservice that 
  consumes training events from `gym-crm` via JMS and tracks monthly 
  trainer workload summaries in MongoDB.

Each service has its own detailed README with architecture overview, 
API documentation, and setup instructions.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4, Java 21 |
| Security | Spring Security 6, JWT |
| Persistence | PostgreSQL, JPA/Hibernate, MongoDB |
| Caching | Redis |
| Messaging | ActiveMQ Artemis, Spring JMS |
| Containerization | Docker, Docker Compose |
| Testing | JUnit 5, Mockito, Cucumber BDD, Testcontainers, Awaitility |
| Observability | Spring Boot Actuator, Prometheus |
