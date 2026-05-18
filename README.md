# Fintech Payment Platform (Java + Spring Boot)

## Visao Geral

Este projeto simula uma plataforma de pagamentos de uma fintech, com foco em microsservicos, seguranca com JWT, persistencia isolada por servico e processamento assincrono com Kafka.

A aplicacao contempla:

- Cadastro e autenticacao de usuarios
- JWT + Refresh Token
- Criacao e consulta de contas digitais
- Operacoes de saldo, credito e debito
- Transferencias entre contas
- Processamento de pagamentos assincronos

## Objetivo

Demonstrar habilidades em:

- Java 21 e Spring Boot
- Arquitetura de microsservicos
- Spring Security com JWT
- Clean Architecture
- Event-driven architecture com Kafka
- PostgreSQL com Flyway
- Testes automatizados
- Docker e Docker Compose

## Arquitetura

```text
Client
  v
API Gateway
  v
Auth (JWT)
  v
-----------------------------
User Service
Account Service
Transaction Service
Payment Service
-----------------------------
  v
Kafka (eventos)
  v
Consumers (processamento)
  v
PostgreSQL / Redis
```

## Estado Atual

Servicos implementados:

- `user-service`
  - Cadastro de usuario
  - Login
  - Refresh token
  - Roles `USER` e `ADMIN`
  - Endpoints protegidos com JWT
  - Consulta do usuario autenticado em `/api/v1/me`
  - Consulta administrativa de usuarios em `/api/v1/users/**`

- `account-service`
  - Criacao de conta para usuario autenticado
  - Consulta da propria conta
  - Consulta de conta por id
  - Credito e debito
  - Validacao de saldo
  - Validacao de conta ativa
  - Validacao local de JWT emitido pelo `user-service`

Ainda pendentes:

- `transaction-service`
- `payment-service`
- API Gateway
- Kafka producers/consumers
- Idempotencia e resiliencia do fluxo financeiro

## Bancos De Dados

Cada microsservico deve ser dono exclusivo do seu banco. O projeto usa um container PostgreSQL local com bancos separados:

```text
user-service        -> user_db
account-service     -> account_db
transaction-service -> transaction_db
payment-service     -> payment_db
```

O script de inicializacao fica em:

```text
docker/postgres/init/01-create-service-databases.sql
```

Importante: scripts em `/docker-entrypoint-initdb.d` rodam automaticamente apenas na primeira criacao do volume do PostgreSQL. Se o container/volume ja existir, crie os bancos manualmente ou recrie o volume.

## Estrutura Do Projeto

```text
FintechPaymentPlatform/
|--- docker-compose.yaml
|--- docker/
|   `--- postgres/
|       `--- init/
|--- user-service/
`--- account-service/
```

Cada servico segue uma Clean Architecture simples:

```text
domain          -> regras centrais, entidades, contratos e excecoes
application     -> use cases, commands, results e portas tecnicas
infrastructure  -> Spring Security, JWT, JPA, Flyway e configuracoes
presentation    -> controllers, DTOs e handlers HTTP
```

## Autenticacao E Seguranca

O `user-service` gera access tokens JWT e refresh tokens.

Header esperado:

```http
Authorization: Bearer <access_token>
```

O `account-service` valida o JWT localmente usando o mesmo `jwt.secret`. Ele valida assinatura e expiracao do token, extrai o `sub` como `userId` e usa esse valor como usuario autenticado.

O `account-service` nao acessa o banco do `user-service`. Isso preserva o isolamento entre microsservicos. A verificacao de usuario ativo deve evoluir depois via API Gateway, introspection ou eventos/cache local de usuarios.

## Documentacao Da API

Swagger UI:

```text
user-service:    http://localhost:8081/swagger-ui/index.html
account-service: http://localhost:8082/swagger-ui/index.html
```

OpenAPI JSON:

```text
user-service:    http://localhost:8081/v3/api-docs
account-service: http://localhost:8082/v3/api-docs
```

Principais endpoints atuais:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh-token

GET  /api/v1/me
GET  /api/v1/users/{id}
GET  /api/v1/users?email=

POST /api/v1/accounts
GET  /api/v1/accounts/me
GET  /api/v1/accounts/{id}
POST /api/v1/accounts/{id}/credit
POST /api/v1/accounts/{id}/debit
```

## Infraestrutura Local

O `docker-compose.yaml` sobe:

- PostgreSQL 15
- Zookeeper
- Kafka

Comandos:

```bash
docker compose up -d
```

Rodar os servicos:

```bash
cd user-service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

```bash
cd account-service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Rodar testes:

```bash
cd user-service
./gradlew test
```

```bash
cd account-service
./gradlew test
```

## Fluxo De Transferencia Planejado

```text
transaction-service publica TRANSFER_REQUESTED
account-service consome o evento
account-service realiza debito e credito
account-service publica TRANSFER_COMPLETED
transaction-service consome confirmacao
transaction-service atualiza a transacao para COMPLETED ou FAILED
```

## Proxima Etapa

Implementar Kafka e iniciar o `transaction-service`, mantendo banco proprio, migrations com Flyway, endpoints documentados e testes automatizados.

## Autor

Ailton Martins  
Backend Developer (Java | Spring Boot | Microsservicos)

LinkedIn: https://www.linkedin.com/in/ailton-martins-1a4277136  
GitHub: https://github.com/ailtonmartins
