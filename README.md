# 📦 Inventory System API

A RESTful **Inventory Management and Point of Sale (POS)** system built with **Spring Boot**, designed to manage products, categories, purchases, and sales with secure authentication and role-based access control.

This project focuses on clean architecture, security best practices, and clear API documentation.

---

## ✨ Features

### Inventory Management
- Create, update, delete, and list articles
- Stock and price control
- Measurement units and optional product images

### Category Management
- Create and manage product categories
- Enable or disable categories

### Point of Sale (POS)
- Register sales transactions
- Supported payment methods:
  - CASH
  - YAPPY
  - TRANSFER

### User & Role Management
- Role-based access control
- Supported roles:
  - `ADMIN`
  - `CASHIER`

### Authentication & Security
- JWT-based authentication
- Access Token & Refresh Token strategy
- Secure logout and token revocation
- Spring Security integration

### API Documentation
- Fully documented using **Swagger / OpenAPI 3**

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- JWT (Access & Refresh Tokens)
- Spring Data JPA
- Hibernate
- PostgreSQL
- Swagger / OpenAPI 3
- Maven

---

## 🔐 Authentication & Authorization

This API uses **JWT authentication** to secure endpoints.

### Access Token
- Required to access protected resources
- Short-lived

### Refresh Token
- Used to generate new access tokens
- Stored securely and revocable

### Roles

| Role | Description |
|------|------------|
| ADMIN | Full system access |
| CASHIER | Sales and inventory operations |

## 📘 API Documentation (Swagger)

The API is fully documented using **Swagger UI**.

Once the application is running, access the documentation at:

http://localhost:8080/swagger-ui.html


---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

---

### Environment Variables

Configure the following properties:

```properties
DB_URL=jdbc:postgresql://localhost:5432/inventory_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_base64_secret_key
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000
```

## 📂 Project Structure

```text
src/main/java/com/inventory/inventorySystem
├── config
├── controller
├── dto
├── enums
├── exceptions
├── mapper
├── model
├── repository
├── security
├── service
├── utils
├── InventorySystemApplication.java
```
