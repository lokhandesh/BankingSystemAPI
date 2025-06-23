# 🏦 BankingSystemAPI

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen.svg)
![Build](https://img.shields.io/github/actions/workflow/status/lokhandesh/BankingSystemAPI/maven.yml?label=build)
![License](https://img.shields.io/github/license/lokhandesh/BankingSystemAPI)
![Docker Pulls](https://img.shields.io/docker/pulls/lokhandesh/banking-api?style=flat-square)

A secure, production-grade RESTful Banking System API built with **Spring Boot**, **PostgreSQL**, and **Docker**. Includes robust banking features like JWT authentication, transactions, statement PDFs, and AWS S3 integration.

---

## 📸 Swagger UI

![Swagger Screenshot](https://github.com/lokhandesh/BankingSystemAPI/blob/main/swaggerscreen.png)

Explore all endpoints interactively using Swagger:  
**URL:** `http://localhost:8080/swagger-ui.html`

---

## 🚀 Features

- ✅ User Registration & Login (JWT-secured)
- ✅ Role-based Security (Spring Security)
- ✅ Create & Manage Bank Accounts
- ✅ Deposit, Withdraw & Transfer Money
- ✅ Generate PDF Statements
- ✅ Upload to AWS S3 & Email Download Link
- ✅ Pagination, Sorting, Filtering
- ✅ Input Validation & Global Error Handling
- ✅ DB Versioning with Flyway
- ✅ Dockerized for Easy Deployment

---

## 🛠️ Tech Stack

| Layer        | Technology                   |
|--------------|-------------------------------|
| Language     | Java 17                       |
| Framework    | Spring Boot, Spring Security  |
| Database     | PostgreSQL                    |
| ORM          | Hibernate / Spring Data JPA   |
| Auth         | JWT Token                     |
| DB Migration | Flyway                        |
| Storage      | AWS S3                        |
| PDF Export   | iText                         |
| Email        | JavaMail                      |
| Container    | Docker, Docker Compose        |
| Build Tool   | Maven                         |

---

## 🧾 API Overview

### 🔐 Authentication

| Endpoint             | Method | Description         |
|----------------------|--------|---------------------|
| `/api/auth/register` | POST   | Register a new user |
| `/api/auth/login`    | POST   | Login & get JWT     |

### 🧾 Accounts

| Endpoint             | Method | Description              |
|----------------------|--------|--------------------------|
| `/api/accounts`      | POST   | Create a new account     |
| `/api/accounts/{id}` | GET    | Get account balance/info |

### 💸 Transactions

| Endpoint                      | Method | Description        |
|-------------------------------|--------|--------------------|
| `/api/transactions/deposit`  | POST   | Deposit money       |
| `/api/transactions/withdraw` | POST   | Withdraw money      |
| `/api/transactions/transfer` | POST   | Transfer money      |

### 📄 Statements

| Endpoint                         | Method | Description                      |
|----------------------------------|--------|----------------------------------|
| `/api/statements/download`      | GET    | Download PDF statement           |
| `/api/statements/email`         | POST   | Email statement link (S3 upload) |

---

## 🐳 Docker Setup

### 1. Build & Run with Docker Compose

```aiignore
git clone https://github.com/lokhandesh/BankingSystemAPI.git
```
```aiignore
cd BankingSystemAPI
```

```bash
   docker-compose up --build
```
---

## 🚀 AWS EC2: Production Deployment (Using Docker Compose)

Easily deploy the BankingSystemAPI on a secure AWS EC2 instance using Docker Compose.

### ✅ Prerequisites

- An **Ubuntu-based EC2 instance**
- **Docker & Docker Compose** installed
- Your Docker image is **pushed to DockerHub**
- Optional: PostgreSQL running on the EC2 instance or via Amazon RDS

---

### 1️⃣ SSH into EC2

```bash
   ssh ubuntu@<EC2_PUBLIC_IP>
```

# Access the Application

http://35.174.104.223:8080

http://35.174.104.223:8080/swagger-ui.html