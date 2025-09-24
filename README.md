# Transaction Service

A Spring Boot microservice for managing financial transactions.  
The service allows creating accounts and recording transactions with different operation types (e.g., purchase, payment, withdrawal).

---

## 🚀 Features
- Create and manage accounts
- Support multiple operation types (e.g., PURCHASE, PAYMENT, WITHDRAWAL)
- Record and retrieve transactions
- Input validations (e.g., amount > 0, valid account & operation type)
- Exception handling for missing entities

---

## 🛠️ Tech Stack
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 / PostgreSQL (configurable)
- JUnit 5 + Mockito
- Maven

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional, for running DB)

### Clone the repository
```bash
git clone https://github.com/your-username/transaction-service.git
cd transaction-pismo
```
---

## ⚙️ Getting Started
```
mvn spring-boot:run
```
📖 API Endpoints
Create Account
```
curl --location 'http://localhost:8080/api/v1/accounts/create' \
--header 'Content-Type: application/json' \
--data '{"documentNumber":"12345678903"}'
```
POST /accounts
```
curl --location 'http://localhost:8080/api/v1/accounts/create' \
--header 'Content-Type: application/json' \
--data '{"documentNumber":"12345678903"}'
```
POSt /create
```
curl --location 'http://localhost:8080/api/v1/transactions/create' \
--header 'Content-Type: application/json' \
--data '{
           "accountId": 1,
           "operationTypeId": 1,
           "amount": 100.50
         }'
```

✅ Testing

Run all tests with:
```
mvn test
```


Key test cases:

Create transaction successfully

Fail when account not found

Fail when operation type not found

Fail when amount is zero or negative

```
🐳 Run with Docker (optional)
docker build -t transaction-service .
docker run -p 8080:8080 transaction-service
```

📌 Future Improvements

Add pagination & filters for transaction history

Integrate with external payment APIs

Add authentication & authorization

Enhance observability (metrics, tracing)

👨‍💻 Author

Hemant Bhardwaj
Senior Software Engineer | Backend Specialist (Java, Spring, Microservices)
---



