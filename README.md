# Insurance Management System

## 1. Project Overview

The Insurance Management System is a Spring Boot REST API application that manages insurance customers, products, policies, payments, and claims.

The application includes JWT-based authentication, role-based authorization, OTP verification, policy renewal, automatic policy expiry, caching, and claim document management.

It provides REST endpoints for managing insurance operations and supports API exploration through Swagger UI.

## 2. Technology Stack

- Java 17 (Maven compiler configuration)
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Hibernate ORM
- MySQL
- Spring Security
- JSON Web Tokens (JWT)
- Bean Validation
- Caffeine Cache
- Spring Boot Actuator
- SpringDoc OpenAPI / Swagger UI
- Maven
- Eclipse IDE
- Postman

## 3. Main Features

### 3.1 Customer Management

- Create customer records
- Retrieve customer information by ID
- Update customer details
- Delete customer records
- Pagination and sorting
- Audit fields

### 3.2 Insurance Product Management

- Create insurance products
- Retrieve products by ID
- Update product details
- Deactivate products
- Search insurance products
- Filter products by insurance type
- Pagination and sorting
- Caffeine caching
- Administrator-only product modifications

Supported insurance types include:

- HEALTH
- HOME
- LIFE
- MOTOR
- TRAVEL

### 3.3 Authentication and Authorization

- OTP-based authentication flow
- JWT token generation and validation
- Role-based authorization
- CUSTOMER and ADMIN roles
- Password encryption using BCrypt
- Stateless authentication using JWT

Protected endpoints require a valid JWT token. Administrator-only operations require an account with the appropriate role.

### 3.4 Policy Management

- Create insurance policies
- Retrieve policy information
- Retrieve policies using supported search criteria
- Update policy details
- Generate policy numbers
- Renew eligible policies
- Cancel policies
- Track policy status
- Automatically expire eligible policies

Policy renewal currently updates the existing policy record. A separate renewal-history record is not maintained.

### 3.5 Payment Management

- Record payments
- Retrieve payments by ID
- Retrieve payments associated with a policy
- Check for duplicate transaction references

**Note:** Payment recording is a demonstration implementation. It does not integrate with a real payment gateway.

### 3.6 Claims Management

- Submit insurance claims
- Retrieve claims by ID
- Retrieve claims associated with a policy
- Retrieve claims using the available listing endpoint
- Review claims
- Approve or reject claims
- Store claim review notes
- Upload claim documents
- List documents associated with a claim
- Download claim documents
- Validate uploaded document size and supported file types

### 3.7 Automatic Policy Expiry

The application includes scheduled policy expiry processing.

The scheduler identifies eligible active policies whose end dates have passed and updates their status to `EXPIRED`.

The expiry schedule is configurable through application properties.

### 3.8 Caching

The application uses Caffeine as its in-memory caching provider.

Caching is configured for the insurance product and policy cache names. Cache expiration and maximum size are configurable through `application.properties`.

### 3.9 Monitoring

Spring Boot Actuator provides application monitoring endpoints.

The application exposes configured endpoints for:

- Health
- Application information
- Metrics
- Cache information

## 4. Project Structure

The main Java package is:

`com.example.insurance`

The project uses a layered architecture.

```text
InsuranceManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/insurance/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       ├── entity/
│   │   │       ├── enums/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       ├── scheduler/
│   │   │       ├── security/
│   │   │       ├── service/
│   │   │       └── service/impl/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

The structure above represents the main application layers. Additional classes and packages may exist in the project.

### Package Responsibilities

- **config:** Application and framework configuration.
- **controller:** REST API endpoints.
- **dto.request:** Incoming request data structures.
- **dto.response:** Response data structures.
- **entity:** JPA entities mapped to database tables.
- **enums:** Enumeration types used by the application.
- **exception:** Exception handling.
- **mapper:** Mapping between entities and DTOs.
- **repository:** Database access using Spring Data JPA.
- **scheduler:** Scheduled background processing.
- **security:** JWT authentication and related security components.
- **service:** Business service interfaces.
- **service.impl:** Business service implementations.

## 5. Database Configuration

The application uses MySQL.

Database name:

`insurance_db`

The database connection is configured through the following properties:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/insurance_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:change-me}
```

The example username and fallback password are intended for local configuration only. Set your actual database credentials through environment variables.

Create the database before starting the application:

```sql
CREATE DATABASE insurance_db;
```

Hibernate schema configuration:

```properties
spring.jpa.hibernate.ddl-auto=update
```

The application uses Spring Data JPA and Hibernate for database operations.

**Note:** The `update` setting is convenient for development. Production deployments should use a controlled database migration strategy.

## 6. Prerequisites

Before running the application, install or configure:

1. A compatible JDK.
2. MySQL Server.
3. Maven, or use the Maven Wrapper included with the project.
4. An IDE such as Eclipse, if preferred.
5. Postman for API testing.

The Maven compiler configuration specifies Java 17. Use a compatible JDK to build the application.

## 7. Running the Application

### Step 1: Create the database

Start MySQL and execute:

```sql
CREATE DATABASE insurance_db;
```

### Step 2: Configure the environment variables

Configure the following variables using your local terminal or IDE run configuration:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Example values for local development:

```text
DB_URL=jdbc:mysql://localhost:3306/insurance_db
DB_USERNAME=root
DB_PASSWORD=your-local-mysql-password
JWT_SECRET=your-secure-random-secret
```

Do not commit real passwords, JWT secrets, or other credentials to GitHub.

Generate a random JWT secret on macOS or Linux with:

```bash
openssl rand -base64 32
```

Use the generated value for your local `JWT_SECRET` environment variable.

Keep the secret stable while testing. Changing it invalidates tokens signed with the previous secret.

### Step 3: Import the project

In Eclipse:

1. Select **File → Import**.
2. Choose **Existing Maven Projects**.
3. Select the project directory.
4. Finish the import.
5. Allow Maven dependencies to resolve.

### Step 4: Run the tests

From the project root, execute:

```bash
./mvnw test
```

On Windows, use:

```bat
mvnw.cmd test
```

### Step 5: Start the application

Run the main application class:

`InsuranceManagementSystemApplication`

Alternatively, execute:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

Confirm that the application starts successfully and connects to MySQL.

### Application URL

The default application port is `8080`.

Base URL:

http://localhost:8080

## 8. Swagger UI

The application uses SpringDoc OpenAPI to document its REST endpoints.

Open Swagger UI while the application is running:

http://localhost:8080/swagger-ui.html

The API specification is available at:

http://localhost:8080/v3/api-docs

Swagger UI can be used to explore the available endpoints and examine request and response formats.

Protected endpoints still require the appropriate authentication.

## 9. Actuator Health

The health endpoint is:

http://localhost:8080/actuator/health

This endpoint provides application health information.

Other configured Actuator endpoints include application information, metrics, and caches.

Actuator access should be appropriately restricted when deploying the application outside a local development environment.

## 10. Main API Endpoints

The following list summarizes the endpoints documented for the application. Consult the controllers and Swagger UI for the exact mappings, request fields, and response structures.

### 10.1 Authentication

Base path:

`/api/auth`

The authentication flow includes account registration, OTP requests, and OTP verification to obtain a JWT.

Consult `AuthController` for the exact endpoint paths and request bodies.

### 10.2 Customers

Base path:

`/api/customers`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/customers` | Create a customer |
| GET | `/api/customers/{id}` | Retrieve a customer |
| PUT | `/api/customers/{id}` | Update a customer |
| DELETE | `/api/customers/{id}` | Delete a customer |
| GET | `/api/customers` | List customers with pagination and sorting |

### 10.3 Products

Base path:

`/api/products`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/products` | Create a product |
| GET | `/api/products/{id}` | Retrieve a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Deactivate a product |
| GET | `/api/products` | List products |
| GET | `/api/products/type/{insuranceType}` | Filter by insurance type |
| GET | `/api/products/search` | Search products using supported criteria |

Product creation, updating, and deactivation require the ADMIN role.

### 10.4 Policies

Base path:

`/api/policies`

The documented policy operations include:

- Creating policies.
- Retrieving policies.
- Updating policies.
- Cancelling policies.
- Renewing eligible policies.
- Retrieving policies using supported search criteria.

Renewal endpoint:

`POST /api/policies/{id}/renew`

Refer to `InsurancePolicyController` and Swagger UI for the complete list of supported policy endpoints.

### 10.5 Payments

Base path:

`/api/payments`

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/payments` | Record a payment |
| GET | `/api/payments/{id}` | Retrieve a payment |
| GET | `/api/payments/policy/{policyId}` | Retrieve payments associated with a policy |

Payment recording is a demonstration workflow and does not process transactions through a real payment gateway.

### 10.6 Claims

Base path:

`/api/claims`

The documented claim operations include:

- Submitting a claim.
- Retrieving a claim by ID.
- Retrieving claims associated with a policy.
- Listing claims.
- Reviewing claims.

The review endpoint documented for the application is:

`PUT /api/claims/{id}/review`

Consult `InsuranceClaimController` and Swagger UI for the exact supported mappings and authorization requirements.

### 10.7 Claim Documents

Claim document functionality includes uploading, listing, and downloading documents associated with claims.

The configured storage directory is:

`uploads/claim-documents`

Uploaded documents are local application files. They should not be committed to the Git repository.

## 11. Authentication and API Testing

Postman can be used to test the REST APIs.

### Testing workflow

1. Start the application.
2. Register or use an appropriate account.
3. Complete the configured OTP authentication flow.
4. Obtain a valid JWT token.
5. Open Postman.
6. Select the required HTTP method and endpoint.
7. Open the Authorization tab.
8. Select **Bearer Token**.
9. Enter the JWT token.
10. Add the request body when required.
11. Send the request and inspect the response.

Use an ADMIN token for administrator-only operations.

A CUSTOMER token should not be used to test operations restricted to administrators.

### HTTP status codes

Common HTTP status codes include:

- `200 OK`: Request completed successfully.
- `201 Created`: A resource was created.
- `204 No Content`: Request completed without a response body.
- `400 Bad Request`: Invalid request data.
- `401 Unauthorized`: Missing or invalid authentication.
- `403 Forbidden`: Authenticated user lacks the required permission.
- `404 Not Found`: Requested resource was not found.
- `500 Internal Server Error`: An unexpected server-side error occurred.

The actual status code depends on the endpoint and its implementation.

## 12. Policy Renewal

Policy renewal is available through:

`POST /api/policies/{id}/renew`

The current implementation renews the existing policy record.

It does not maintain a separate renewal-history record.

The renewal operation should be tested using an eligible policy and the required authorization.

## 13. Automatic Policy Expiry

The policy expiry schedule is configured through:

```properties
app.policy.expiry-cron=0 0 1 * * *
```

This cron expression represents a daily execution at 1:00 AM in the scheduler's configured timezone.

The scheduler checks eligible policies and updates expired policies to the `EXPIRED` status.

The exact processing behavior depends on the implementation in the scheduler and its associated service.

## 14. File Upload Configuration

The application configures claim document storage and request size limits through:

```properties
insurance.file.upload-dir=uploads/claim-documents
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
```

The upload directory is local to the application environment.

For a deployed environment, configure suitable storage permissions, access controls, and backup procedures.

Uploaded documents may contain sensitive information. Do not publish customer documents or other private files to a public repository.

## 15. Important Security Considerations

- Use a securely generated JWT secret.
- Supply credentials through environment variables or a secure configuration mechanism.
- Do not commit passwords, tokens, or secrets to GitHub.
- Keep OTP console logging disabled outside local development.
- Ensure administrator-only operations are protected by the security configuration.
- Implement ownership checks for customer-specific policies, payments, and claims.
- Do not trust client-supplied payment statuses in a production payment workflow.
- Integrate and validate a real payment gateway before handling actual payments.
- Restrict Swagger UI and Actuator endpoints appropriately in production.
- Use controlled database migrations for production deployments.
- Test authentication, authorization, and business rules with automated tests.
- Validate uploaded files and restrict access to sensitive claim documents.

## 16. Testing

The project includes automated tests for selected service operations.

Run the test suite using:

```bash
./mvnw test
```

Review the Maven output to confirm whether all tests pass.

The presence of a test class does not guarantee complete coverage of the application. Authentication, authorization, customer ownership, policy workflows, payment processing, claim processing, and file uploads should be covered by appropriate tests.

## 17. Future Enhancements

Potential enhancements include:

- Customer ownership-based authorization.
- Email or SMS integration for OTP delivery.
- Real payment gateway integration.
- Separate policy renewal history.
- Additional automated integration and security tests.
- Production-ready database migrations.
- Improved file storage and document security.
- Centralized logging and monitoring.
- Deployment using a cloud or container platform.

## 18. Disclaimer

This project is a demonstration Insurance Management System built using Spring Boot and related technologies.

It demonstrates REST API development, database persistence, authentication, authorization, caching, scheduled processing, and document management.

Additional security controls, payment gateway integration, production configuration, and comprehensive automated testing are required before using the application to manage real insurance transactions.