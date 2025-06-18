# ThoughtNest – Backend

This is the backend service of ThoughtNest – a secure blogging platform built with Spring Boot, using a layered architecture and JWT-based authentication.

## Technology Stack

* **Java 17** – Main programming language
* **Spring Boot** – Application framework for building RESTful services
* **Spring Security** – Handles authentication and authorization using JWT
* **PostgreSQL** – Primary database for storing users and articles
* **H2 Database** – Used for integration testing
* **Maven** – Build and dependency management tool
* **JUnit & Mockito** – Testing frameworks for unit and integration testing

## Project Structure

```
com.thoughtNest.backend
│
├── controller           # Handles HTTP requests and responses
├── service              # Business logic layer
├── repository           # Data access layer (Spring Data JPA)
├── model                # Domain models (entities)
├── dto                  # Data Transfer Objects for request/response formatting
├── security             # JWT filters, utility classes, and UserDetails service
├── util                 # Common utilities like response formatting
├── exceptions           # Custom exceptions and handlers
└── config               # CORS and security configuration
```

## Key Features and Functionality

### 1. **Authentication & Authorization**

* Users can register and log in using email and password
* Passwords are securely hashed using BCrypt
* JWT tokens are issued on login and required for protected endpoints

### 2. **Email Service**

* Sends password reset links via email
* Uses `JavaMailSender` with a MIME message
* Reset links include a time-bound token stored in the database

### 3. **Password Reset Flow**

* User requests a reset with their email
* A unique token is generated, stored, and emailed
* User uses the token to set a new password before expiry
* Expired or invalid tokens are rejected

### 4. **Articles**

* Users can create, update, delete, and view their own articles
* Public users can view published articles
* Rich text content is stored as HTML

### 5. **Validation and Exception Handling**

* Global exception handler standardizes error responses
* Custom exceptions like `UserNotFoundException`, `InvalidTokenException`, etc.
* DTO-level validation using annotations

### 6. **Testing**

* Unit tests cover all major service logic using Mockito
* Integration tests for authentication, protected routes, and database operations using MockMvc and H2

### 7. **Security Filters**

* `JwtAuthenticationFilter` handles login and token generation
* `JwtAuthorizationFilter` validates token on each request
* CSRF disabled, stateless session management

## How to Run the Backend

### 1. **Set up PostgreSQL locally or use Docker**

Create a database named `thoughtnest` (or configure your own name in `application.properties`).

### 2. **Update environment variables**

Set the following in your local environment or a `.env` file (if using a loader):

* `SPRING_DATASOURCE_URL`
* `SPRING_DATASOURCE_USERNAME`
* `SPRING_DATASOURCE_PASSWORD`
* `JWT_SECRET`
* `EMAIL_USERNAME`
* `EMAIL_PASSWORD`

### 3. **Build and run**

mvn clean install
mvn spring-boot:run
```

### 4. **Run tests**

mvn test
```

## Docker Support

This backend has a Dockerfile with a multi-stage setup.

### To build and run:

docker build -t thoughtnest-backend .
docker run -p 8080:8080 thoughtnest-backend
```

Ensure that your PostgreSQL is accessible to the container, or use `docker-compose`.

### Important Endpoints

### Authentication

* `POST /api/auth/register`
* `POST /api/auth/login`
* `POST /api/auth/request-reset`
* `POST /api/auth/reset-password`

### Articles

* `GET /api/articles/public` – View all published articles
* `GET /api/articles/my` – View logged-in user's articles
* `POST /api/articles` – Create a new article
* `PUT /api/articles/{id}` – Update an article
* `DELETE /api/articles/{id}` – Delete an article

### Contact Form

* `POST /api/contact` – Submit user feedback

## Notes

* All protected routes require a valid JWT in the `Authorization` header
* Application profile is set to `test` during test runs to isolate from production DB
* Sensitive data is not hardcoded, all secrets are loaded via environment variables

## Final Thoughts

This backend is built with scalability, security, and clarity in mind. It separates responsibilities cleanly across layers and follows best practices for modern Spring Boot development. The goal is to keep the structure extensible for future features like comments, likes, or admin dashboards.

## Legal and Ownership Notice

This project is created and owned by **Harinee Anandh**.

All code, structure, styling, and logic belong exclusively to the author.
This work must not be copied, reused, republished, or redistributed under any circumstance.

Any unauthorized use or duplication will be treated as a violation of intellectual property rights and may result in legal consequences.

## Contact
If you wish to reach out regarding this work, collaboration, or clarification, please use the contact form inside the app or reach out via appropriate professional channels.

## Crafted with originality and purpose by **Harinee Anandh**
# Trigger backend CI
