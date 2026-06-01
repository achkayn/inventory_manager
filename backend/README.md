# Inventory Manager Backend

Backend API for a mini stock and inventory management system built with Spring Boot.

It provides JWT-based authentication and CRUD APIs for:

- Users and authentication
- Categories
- Products
- Suppliers
- Orders
- Dashboard statistics

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- PostgreSQL
- Maven
- Docker Compose for local PostgreSQL

## Features

- Register and login with JWT authentication
- Secure all business endpoints with bearer tokens
- Manage categories, products, suppliers, and orders
- Filter products by low stock threshold
- Filter orders by status
- View dashboard summary and category stock information
- Persist data in PostgreSQL using JPA/Hibernate

## Project Structure

```text
src/main/java/com/inventorymanager
|-- auth
|-- category
|-- common
|-- config
|-- dashboard
|-- order
|-- product
`-- supplier
```

## Requirements

- JDK 17
- Maven 3.9+
- PostgreSQL 17+ or Docker

## Configuration

The application uses `src/main/resources/application.properties`.

Default local settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventorydb
spring.datasource.username=postgres
spring.datasource.password=password
jwt.secret=change-this-secret-key-please-use-a-longer-secret-key-1234567890
jwt.expiration=86400000
```

Notes:

- Change the JWT secret before using the app in production.
- `spring.jpa.hibernate.ddl-auto=update` is enabled, so the schema updates automatically on startup.
- The app listens on the default Spring Boot port `8080` unless you change it.

## Run PostgreSQL with Docker

Start the database with:

```bash
docker compose -f docker-compose.yaml up -d
```

This creates:

- Database: `inventorydb`
- User: `postgres`
- Password: `password`
- Port: `5432`

## Run the Application

### Option 1: Run from Maven

```bash
mvn spring-boot:run
```

### Option 2: Build and run the JAR

```bash
mvn clean package
java -jar target/inventory-manager-0.0.1-SNAPSHOT.jar
```

## Authentication

Public endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`

All other endpoints require:

```http
Authorization: Bearer <your-jwt-token>
```

The JWT contains the user email as the subject and a `role` claim.

## API Overview

All responses follow a common wrapper:

```json
{
  "success": true,
  "message": "Request message",
  "data": {}
}
```

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Categories

- `GET /api/categories`
- `GET /api/categories/{id}`
- `POST /api/categories`
- `PUT /api/categories/{id}`
- `DELETE /api/categories/{id}`

### Products

- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/low-stock?threshold=25`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Suppliers

- `GET /api/suppliers`
- `GET /api/suppliers/{id}`
- `POST /api/suppliers`
- `PUT /api/suppliers/{id}`
- `DELETE /api/suppliers/{id}`

### Orders

- `GET /api/orders`
- `GET /api/orders?status=PENDING`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `PATCH /api/orders/{id}/status`
- `DELETE /api/orders/{id}`

### Dashboard

- `GET /api/dashboard/summary`
- `GET /api/dashboard/category-stock`

## Domain Model

- `Category`: name, description
- `Product`: name, description, price, stock quantity, low stock threshold, category
- `Supplier`: name, email, phone, address
- `Order`: supplier, product, quantity, status, createdAt
- `User`: username, email, password, role

## Security

- CSRF is disabled because the API is stateless.
- Session management is stateless.
- JWT authentication is applied through a custom filter.
- Passwords are stored using BCrypt hashing.

## Response and Error Handling

The app uses a shared API response format and a global exception handler for consistent error responses.

## Recommended Next Step

If you want, I can also add:

1. A more detailed `README.md` with sample request/response payloads for each endpoint.
2. A `Postman` collection outline for testing the API.
3. A `docker-compose` setup that includes the backend service too.
