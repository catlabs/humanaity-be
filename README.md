# Humanaity Backend

Spring Boot 3.5 REST API that manages cities and their humans, secures access with JWT (access + refresh tokens), and can generate inhabitants through OpenAI (with a Faker fallback when AI is unavailable).

## Tech Stack
- Java 17, Spring Boot 3.5 (web MVC)
- Spring Security (stateless JWT with `OncePerRequestFilter`)
- Spring Data JPA + H2 file database
- Springdoc OpenAPI 3 (`/swagger-ui.html`)
- Spring AI (OpenAI ChatClient) + Java Faker fallback
- Lombok for boilerplate reduction

## What the API Does
- **Auth**: signup, login, refresh, logout; passwords hashed with BCrypt; refresh tokens persisted and rotated.
- **Cities**: CRUD with ownership; city creation auto-generates humans using AI (or Faker fallback).
- **Humans**: CRUD with traits (creativity, intellect, sociability, practicality), positions, and personality derivation.
- **Simulation**: background scheduler that moves humans, detects collisions, and persists updates; start/stop/status per city.
- **Docs & tooling**: OpenAPI/Swagger UI enabled; H2 console exposed for local debugging.

## API Surface (summary)
- Auth (public):  
  - `POST /auth/signup`  
  - `POST /auth/login`  
  - `POST /auth/refresh`  
  - `POST /auth/logout`
- Cities (JWT required except where noted):  
  - `GET /api/cities`  
  - `GET /api/cities/mine`  
  - `GET /api/cities/search?name=foo`  
  - `GET /api/cities/{id}`  
  - `POST /api/cities` (creates city + generates humans)  
  - `PUT /api/cities/{id}`  
  - `DELETE /api/cities/{id}`
- Humans (JWT):  
  - `GET /api/humans/{id}`  
  - `GET /api/humans/city/{cityId}`  
  - `POST /api/humans`  
  - `PUT /api/humans/{id}`  
  - `DELETE /api/humans/{id}`
- Simulation (JWT):  
  - `POST /api/simulations/{cityId}/start`  
  - `POST /api/simulations/{cityId}/stop`  
  - `GET /api/simulations/{cityId}/status`

All `/api/**` routes expect `Authorization: Bearer <access_token>`.

## Configuration
Key properties in `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE
spring.jpa.hibernate.ddl-auto=update

# Swagger / OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# JWT
jwt.secret=change-me-in-prod
jwt.access-token-expiration=900000      # 15 minutes
jwt.refresh-token-expiration=604800000  # 7 days

# OpenAI (optional)
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4-turbo
```

## Local Run
```bash
cd humanaity-be
./mvnw spring-boot:run
```
Then open `http://localhost:8080/swagger-ui.html` for interactive docs.  
H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:file:./data/testdb`, user `sa`, empty password by default).

## Testing
```bash
./mvnw test
```
