```markdown
# Support Ticket System

A Spring Boot application for managing support tickets, using PostgreSQL for persistence and Kafka for messaging.

## Prerequisites
- **Java 17** or later
- **Maven** 3.8.0 or later
- **Docker** and **Docker Compose**

## Setup and Running Locally
1. **Start Docker Compose**:
   ```bash
   docker-compose up -d
   ```
   This starts PostgreSQL, ZooKeeper, and Kafka.

2. **Run the Spring Boot Application**:
   ```bash
   mvn spring-boot:run
   ```
   The application will be available at `http://localhost:8080`.

3. **Test Endpoints**:
   - Create a ticket:
     ```bash
     curl -X POST http://localhost:8080/tickets -H "Content-Type: application/json" -d '{"title":"Test Ticket","description":"Test Description"}'
     ```
   - Get a ticket by ID:
     ```bash
     curl http://localhost:8080/tickets/{id}
     ```

## Running Tests
1. **Unit and Integration Tests**:
   Run tests with the `test` profile, using H2 and embedded Kafka:
   ```bash
   mvn clean test
   ```
