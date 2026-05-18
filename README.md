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
-----------------------------
  v
Kafka (eventos)
  v
Account Service Consumer
  v
PostgreSQL
```

## Estado Atual

Servicos implementados:

- `api-gateway`
  - Porta publica `8080`
  - Roteamento para `user-service`, `account-service` e `transaction-service`
  - Validacao local de JWT antes de encaminhar rotas protegidas
  - Encaminhamento de headers internos `X-Authenticated-User-Id` e `X-Authenticated-User-Email`
  - Rotas publicas para login, cadastro, refresh token, health check e OpenAPI JSON dos servicos
  - Swagger UI centralizada dos servicos

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
  - Consumer Kafka de solicitacao de transferencia
  - Debito da conta origem e credito da conta destino
  - Producer Kafka de resultado da transferencia

- `transaction-service`
  - Criacao de transferencia com status `PENDING`
  - Consulta de transacao por id
  - Producer Kafka de solicitacao de transferencia
  - Consumer Kafka de resultado da transferencia
  - Atualizacao da transacao para `COMPLETED` ou `FAILED`

Ainda pendentes:

- Idempotencia e resiliencia do fluxo financeiro
- Retry e DLQ para consumidores Kafka

## Bancos De Dados

Cada microsservico deve ser dono exclusivo do seu banco. O projeto usa um container PostgreSQL local com bancos separados:

```text
user-service        -> user_db
account-service     -> account_db
transaction-service -> transaction_db
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
|--- account-service/
|--- transaction-service/
`--- api-gateway/
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

O `api-gateway` valida JWT antes de encaminhar rotas protegidas. Os servicos internos ainda mantem validacao JWT propria, funcionando como defesa adicional enquanto nao houver um contrato final de autenticacao interna.

## Documentacao Da API

Swagger UI centralizada:

```text
api-gateway: http://localhost:8080/swagger-ui/index.html
```

Swagger UI direta por servico, quando rodar os servicos fora do Compose expondo suas portas:

```text
user-service:    http://localhost:8081/swagger-ui/index.html
account-service: http://localhost:8082/swagger-ui/index.html
transaction-service: http://localhost:8083/swagger-ui/index.html
```

OpenAPI JSON:

```text
via gateway:
user-service:    http://localhost:8080/user-service/v3/api-docs
account-service: http://localhost:8080/account-service/v3/api-docs
transaction-service: http://localhost:8080/transaction-service/v3/api-docs

direto no servico:
user-service:    http://localhost:8081/v3/api-docs
account-service: http://localhost:8082/v3/api-docs
transaction-service: http://localhost:8083/v3/api-docs
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

POST /api/v1/transactions/transfers
GET  /api/v1/transactions/{id}
```

## Infraestrutura Local

O `docker-compose.yaml` sobe:

- PostgreSQL 15
- Zookeeper 7.6.1
- Kafka 7.6.1
- Kafka UI
- `user-service`
- `account-service`
- `transaction-service`
- `api-gateway`

Comandos:

```bash
docker compose up -d
```

Endpoint publico principal:

```text
http://localhost:8080
```

Kafka UI:

```text
http://localhost:8090
```

Os servicos internos nao publicam `8081`, `8082` e `8083` no host quando executados via Docker Compose. Eles ficam acessiveis dentro da rede Docker e devem ser chamados pelo gateway.

Rodar os servicos:

```bash
cd user-service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

```bash
cd account-service
./gradlew bootRun --args='--spring.profiles.active=dev'
```

```bash
cd transaction-service
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

```bash
cd transaction-service
./gradlew test
```

## Fluxo De Transferencia Com Kafka

```text
1. Client chama POST /api/v1/transactions/transfers pelo API Gateway.
2. transaction-service cria a transacao com status PENDING.
3. transaction-service publica o evento transaction.transfer.requested.
4. account-service consome transaction.transfer.requested.
5. account-service valida contas, saldo e conta ativa.
6. account-service debita a origem e credita o destino em transacao de banco.
7. account-service publica:
   - transaction.transfer.completed
   - ou transaction.transfer.failed
8. transaction-service consome o resultado.
9. transaction-service atualiza a transacao para COMPLETED ou FAILED.
```

Topicos Kafka atuais:

```text
transaction.transfer.requested
transaction.transfer.completed
transaction.transfer.failed
```

## Proxima Etapa

Implementar idempotencia, retry e DLQ no fluxo financeiro. O ponto principal e evitar processamento duplicado da mesma `transactionId`, especialmente nos consumers Kafka do `account-service` e do `transaction-service`.

## Autor

Ailton Martins  
Backend Developer (Java | Spring Boot | Microsservicos)

LinkedIn: https://www.linkedin.com/in/ailton-martins-1a4277136  
GitHub: https://github.com/ailtonmartins
