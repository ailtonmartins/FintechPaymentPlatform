# 💳 Fintech Payment Platform (Java + Spring Boot)

## 📌 Visão Geral

Este projeto simula uma plataforma de pagamentos de uma fintech, com foco em **microsserviços**, **segurança com JWT**, e **processamento assíncrono com Kafka**.

A aplicação permite:

* Cadastro de usuários
* Autenticação segura (JWT + Refresh Token)
* Criação de contas digitais
* Transferências entre contas
* Processamento de pagamentos assíncronos

---

## 🎯 Objetivo

Demonstrar habilidades avançadas em:

* Java + Spring Boot
* Arquitetura de microsserviços
* Segurança (JWT + Spring Security)
* Event-driven architecture (Kafka)
* Boas práticas (SOLID, Clean Architecture)

---

## 🧠 Arquitetura

```text
Client
  ↓
API Gateway
  ↓
Auth (JWT)
  ↓
-----------------------------
User Service
Account Service
Transaction Service
Payment Service
-----------------------------
  ↓
Kafka (eventos)
  ↓
Consumers (processamento)
  ↓
PostgreSQL / Redis
```

---


## 🔐 Autenticação e Segurança

O sistema utiliza autenticação baseada em **JWT (JSON Web Token)** com suporte a **Refresh Token**.

### 🔑 Fluxo de autenticação

1. Usuário realiza login
2. Sistema gera:

   * Access Token (JWT - curto prazo)
   * Refresh Token (longo prazo)
3. Requisições autenticadas via header:

```http
Authorization: Bearer <access_token>
```

---

### 🔁 Refresh Token

```http
POST /auth/refresh
```

Gera um novo access token sem necessidade de login.

---

### 🔒 Segurança aplicada

* Senhas criptografadas com BCrypt
* Validação de JWT via filtro
* Proteção de endpoints
* Controle de roles (USER / ADMIN)

---

## ⚙️ Tecnologias

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* OpenAPI/Swagger

### Arquitetura

* Microsserviços
* REST APIs
* Event-driven

### Mensageria

* Apache Kafka

### Banco

* PostgreSQL
* Redis (opcional)

### Infra

* Docker
* Docker Compose

---

## 📦 Estrutura do Projeto

```bash
fintech-platform/
│
├── docker-compose.yaml
│
├── user-service/
├── account-service/
├── transaction-service/
└── payment-service/
```

Essa divisão segue uma Clean Architecture simples:

* `domain`: regras centrais, entidades, contratos e exceções.
* `application`: casos de uso específicos por operação, commands, results e portas técnicas.
* `infrastructure`: detalhes técnicos como JWT, Spring Security, JPA e configurações.
* `presentation`: controllers e DTOs da API.

---

## 📚 Documentação da API

O `user-service` usa OpenAPI/Swagger com `springdoc-openapi`.

Com o serviço rodando localmente na porta `8081`, acesse:

```text
Swagger UI: http://localhost:8081/swagger-ui/index.html
OpenAPI JSON: http://localhost:8081/v3/api-docs
```

Endpoints documentados atualmente:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh-token

GET  /api/v1/users/{id}
GET  /api/v1/users?email=
```

As rotas de documentação ficam liberadas no Spring Security:

```text
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

---

## 🔄 Fluxo de Transferência

1. Cliente solicita transferência
2. transaction-service valida saldo
3. Evento `TRANSFER_REQUESTED` é publicado no Kafka
4. account-service processa débito/crédito
5. Evento `TRANSFER_COMPLETED` é emitido

---

## 💥 Resiliência

* Retry automático em falhas
* Idempotência em transações
* Consistência eventual

---

## 🐳 Infraestrutura

```yaml
version: '3.8'

services:

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: fintech
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

---

## 🚀 Como Executar

```bash
docker-compose up -d
```

## 👨‍💻 Autor

**Ailton Martins**
Backend Developer (Java | Spring Boot | Microsserviços)

🔗 LinkedIn: https://www.linkedin.com/in/ailton-martins-1a4277136
💻 GitHub: https://github.com/ailtonmartins

---

## ⭐ Objetivo do Projeto

Este projeto foi criado para demonstrar habilidades em:

* Sistemas distribuídos
* Segurança moderna (JWT)
* Arquitetura escalável
