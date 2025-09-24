# Transaction Service (Pismo Challenge)

A Spring Boot microservice that manages **accounts** and **financial transactions**.  
Built as part of the Pismo challenge, this project demonstrates clean architecture, exception handling, and unit testing in a financial domain use case.

---

## 📑 Table of Contents
1. [Features](#-features)
2. [Tech Stack & Prerequisites](#-tech-stack--prerequisites)
3. [Getting Started](#-getting-started)
4. [API Endpoints](#-api-endpoints)
5. [Testing](#-testing)
6. [Project Structure](#-project-structure)
7. [Future Improvements](#-future-improvements)
8. [Contributing & License](#-contributing--license)

---

## 🚀 Features
- Create and manage **accounts** using a unique document number  
- Record **transactions** with associated operation types  
- Support for positive and negative transaction values (e.g., purchases vs. payments)  
- Centralized exception handling with meaningful error responses  
- Repository layer for persistence (can integrate with H2/PostgreSQL)  
- Unit tests covering both success and failure scenarios  

---

## 🛠 Tech Stack & Prerequisites
- **Java 21**  
- **Spring Boot 3+**  
- **Maven** (for build & dependency management)  
- **H2 Database** (default, in-memory)
- (Optional) **Docker** for containerized deployment  

---

## ⚡ Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Hemantz/Transaction-pismo.git
cd Transaction-pismo
```
---

### ⚙️ Getting Started

## Build Your Jar
```
mvn clean package -DskipTests
```
## Run Spring boot
```
mvn spring-boot:run
```
### OR
## 🐳 Run with Docker (optional)
```
docker build -t transaction-pismo .
docker run -d -p 8080:8080 transaction-pismo
```

### 📖 API Endpoints
## POST Create Account
```
curl --location 'http://localhost:8080/api/v1/accounts/create' \
--header 'Content-Type: application/json' \
--data '{"documentNumber":"12345678903"}'
```
Response
```
{
  "accountId": 1,
  "documentNumber": "12345678900"
}

```
## GET Get Accounts
Request
```
curl --location 'http://localhost:8080/api/v1/accounts/1'
```
Response
```
{
    "accountId": 1,
    "documentNumber": "12345678903"
}
```

## POST Create Transaction
```
curl --location 'http://localhost:8080/api/v1/transactions/create' \
--header 'Content-Type: application/json' \
--data '{
           "accountId": 1,
           "operationTypeId": 1,
           "amount": 100.50
         }'
```
Response
```
{
  "transactionId": 10,
  "accountId": 1,
  "operationTypeId": 4,
  "amount": -123.45,
  "eventDate": "2025-09-15T10:25:32.000Z"
}
```

### ✅ Testing

Run all tests with:
```
mvn test
```


### Key test cases:

Create transaction successfully

Fail when account not found

Fail when operation type not found

Fail when amount is zero or negative

### 📌 Future Improvements

Add pagination & filters for transaction history

Integrate with external payment APIs

Add authentication & authorization

Enhance observability (metrics, tracing)

👨‍💻 Author

Hemant Bhardwaj
Senior Software Engineer | Backend Specialist (Java, Spring, Microservices)
---



