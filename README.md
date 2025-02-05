# Customer Management Service

## Overview
This is a Java Spring Boot service that provides Customer Management functionality, including CRUD operations. It implements JWT-based authentication and authorization using `@HasRole` with two roles:

- **ROLE_ADMIN**: Can perform all operations (Search, Create, Update, Delete).
- **ROLE_USER**: Can perform only Search, Create, and Update operations (Delete is restricted).

Additionally, the service uses **JaCoCo** for test coverage analysis, ensuring at least **90%** coverage in unit tests.

## Technologies Used
- Java 21
- Java Spring Boot (3.4.2)
- Spring Security with JWT Authentication
- Maven
- H2 Database (In-Memory)
- JUnit & Mockito (for Unit Testing)
- JaCoCo (for Test Coverage)

## Installation & Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/KanoknartMeen/customer-service.git
   cd customer-service
   ```
2. Build the project:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Default Users
The system provides two default users for authentication:

| Role       | Username | Password  |
|------------|---------|-----------|
| ROLE_ADMIN | admin   | adminPass |
| ROLE_USER  | user1   | userPass  |

## Database Configuration
This service uses an in-memory **H2 Database**. The database is automatically created and initialized on application startup.

The H2 Console can be accessed at:
```
http://localhost:8080/h2-console
```
Use the JDBC URL: `jdbc:h2:mem:testdb`, username: `sa`, and password: `password` to connect.

## API Endpoints
| Method | Endpoint         | Role Required  | Description        |
|--------|-----------------|---------------|--------------------|
| GET    | /customers      | ROLE_USER, ROLE_ADMIN | Fetch all customers |
| GET    | /customers/{id} | ROLE_USER, ROLE_ADMIN | Fetch a customer by ID |
| POST   | /customers      | ROLE_USER, ROLE_ADMIN | Create a new customer |
| PUT    | /customers/{id} | ROLE_USER, ROLE_ADMIN | Update an existing customer |
| DELETE | /customers/{id} | ROLE_ADMIN     | Delete a customer |

## Authentication & Authorization
This service uses JWT authentication. Users must include a valid token in the `Authorization` header:

```
Authorization: Bearer <jwt-token>
```

### Roles & Permissions
- **ROLE_ADMIN**: Full access to all CRUD operations.
- **ROLE_USER**: Can only search, create, and update customers (cannot delete).

## Running Tests & Code Coverage
To run tests and generate a test coverage report:
```sh
mvn clean test jacoco:report
```

The coverage report will be available at:
```
target/site/jacoco/index.html
```
The test coverage is **90%**.

## License
This project is licensed under the MIT License.

