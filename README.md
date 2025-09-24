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

## 📂 Project Structure
src/
├── main/
│ ├── java/com/pismo/transaction/
│ │ ├── dto/ # DTOs for requests & responses
│ │ ├── entity/ # Entities (Account, Transaction, OperationType)
│ │ ├── repository/ # Spring Data JPA Repositories
│ │ ├── service/ # Service interfaces
│ │ └── service/impl/ # Service implementations
│ └── resources/
│ └── application.yml # Config (DB, server port, etc.)
└── test/
└── java/com/pismo/transaction/ # Unit tests

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

---

## ⚙️ Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (optional, for running DB)

### Clone the repository
git clone <repo link>

mvn spring-boot:run

📖 API Endpoints
Create Account
```
```
POST /accounts
Content-Type: application/json

{
  "documentNumber": "12345678900"
}


Response

{
  "accountId": 1,
  "documentNumber": "12345678900"
}

Create Transaction
POST /transactions
Content-Type: application/json

{
  "accountId": 1,
  "operationTypeId": 4,
  "amount": 123.45
}


Response

{
  "transactionId": 100,
  "accountId": 1,
  "operationTypeId": 4,
  "amount": 123.45,
  "eventDate": "2025-09-15T12:34:56Z"
}
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

Would you like me to also include a **sample Postman collection** (JSON export) in the README so you/your team can quickly test the APIs?



