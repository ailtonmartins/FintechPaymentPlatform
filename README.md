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
- Frontend web simples para operar os fluxos pelo API Gateway

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
- React com TypeScript

## Arquitetura

```text
Client / Frontend React
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

- `frontend`
  - React com TypeScript e Vite
  - Interface operacional na porta `3000`
  - Login, cadastro e armazenamento de JWT + refresh token
  - Refresh token automatico em respostas `401`
  - Telas para usuario, contas, saldo, transferencias e operacao
  - Consumo de todos os fluxos pelo `api-gateway`
  - Nginx para servir o build estatico e fazer proxy para o gateway

- `api-gateway`
  - Porta publica `8080`
  - Roteamento para `user-service`, `account-service` e `transaction-service`
  - Validacao local de JWT antes de encaminhar rotas protegidas
  - Encaminhamento de headers internos `X-Authenticated-User-Id` e `X-Authenticated-User-Email`
  - Rotas publicas para login, cadastro, refresh token, health check e OpenAPI JSON dos servicos
  - Swagger UI centralizada dos servicos
  - Propagacao de `X-Correlation-Id`

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
  - Idempotencia por `transactionId` no processamento financeiro
  - Retry e DLQ no consumer Kafka
  - Outbox para publicacao confiavel de `completed/failed`
  - Endpoints operacionais para resumo, Outbox FAILED e DLQ
  - Metricas Micrometer do fluxo financeiro

- `transaction-service`
  - Criacao de transferencia com status `PENDING`
  - Consulta de transacao por id
  - Producer Kafka de solicitacao de transferencia
  - Consumer Kafka de resultado da transferencia
  - Atualizacao da transacao para `COMPLETED` ou `FAILED`
  - Consumo idempotente de resultados duplicados
  - Retry e DLQ nos consumers Kafka
  - Outbox para publicacao confiavel de `requested`
  - Endpoints operacionais para transacoes pendentes, Outbox FAILED e DLQ
  - Metricas Micrometer do fluxo financeiro

Ainda pendentes:

- Observabilidade do fluxo financeiro
- Reprocessamento operacional de eventos `FAILED` na Outbox

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
|--- api-gateway/
`--- frontend/
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

GET  /api/v1/operations/transactions/summary
GET  /api/v1/operations/transactions/pending
GET  /api/v1/operations/transactions/outbox/failed

GET  /api/v1/operations/accounts/summary
GET  /api/v1/operations/accounts/outbox/failed
```

## Frontend

O projeto possui um frontend simples em React com TypeScript, localizado em:

```text
frontend/
```

A interface roda em:

```text
http://localhost:3000
```

Funcionalidades disponiveis:

- Cadastro de usuario.
- Login com JWT e refresh token.
- Logout.
- Consulta do usuario autenticado em `/api/v1/me`.
- Consulta administrativa de usuarios por id e e-mail.
- Criacao e consulta de contas.
- Operacoes de credito e debito.
- Criacao e consulta de transferencias.
- Dashboard com dados do usuario, conta e resumo financeiro.
- Tela operacional com summaries, transacoes pendentes, Outbox FAILED, DLQ, metricas, health check e OpenAPI dos servicos.

Stack do frontend:

```text
React
TypeScript
Vite
React Router
Axios
TanStack Query
Lucide React
Nginx
Docker
```

O frontend nao chama diretamente `user-service`, `account-service` ou `transaction-service`. Todas as chamadas passam pelo `api-gateway`.

Quando executado via Docker Compose, o Nginx do frontend faz proxy para o gateway nas rotas:

```text
/api/**
/actuator/**
/user-service/**
/account-service/**
/transaction-service/**
```

Isso permite acessar a interface em `http://localhost:3000` sem depender de CORS no gateway.

## Infraestrutura Local

O `docker-compose.yaml` sobe:

- `frontend`
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

Frontend:

```text
http://localhost:3000
```

Kafka UI:

```text
http://localhost:8090
```

Metricas via gateway:

```text
GET /transaction-service/actuator/metrics
GET /transaction-service/actuator/metrics/financial.transactions.pending
GET /transaction-service/actuator/metrics/financial.transactions.failed
GET /transaction-service/actuator/metrics/financial.outbox.pending
GET /transaction-service/actuator/metrics/financial.outbox.failed

GET /account-service/actuator/metrics
GET /account-service/actuator/metrics/financial.transfers.completed
GET /account-service/actuator/metrics/financial.transfers.failed
GET /account-service/actuator/metrics/financial.outbox.pending
GET /account-service/actuator/metrics/financial.outbox.failed
```

Os servicos internos nao publicam `8081`, `8082` e `8083` no host quando executados via Docker Compose. Eles ficam acessiveis dentro da rede Docker e devem ser chamados pelo gateway.

Rodar os servicos:

```bash
cd frontend
npm install
npm run dev
```

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

Build local do frontend:

```bash
cd frontend
npm run build
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
3. transaction-service grava o evento transaction.transfer.requested na tabela outbox_events.
4. Outbox publisher do transaction-service publica o evento no Kafka.
5. account-service consome transaction.transfer.requested.
6. account-service valida contas, saldo e conta ativa.
7. account-service debita a origem, credita o destino e registra a transactionId em processed_transfer_events.
8. account-service grava o resultado na tabela outbox_events.
9. Outbox publisher do account-service publica:
   - transaction.transfer.completed
   - ou transaction.transfer.failed
10. transaction-service consome o resultado.
11. transaction-service atualiza a transacao para COMPLETED ou FAILED.
```

Topicos Kafka atuais:

```text
transaction.transfer.requested
transaction.transfer.completed
transaction.transfer.failed
```

Topicos DLQ atuais:

```text
transaction.transfer.requested.dlq
transaction.transfer.completed.dlq
transaction.transfer.failed.dlq
```

## Idempotencia E Resiliencia

O fluxo financeiro foi ajustado para tolerar entregas duplicadas do Kafka e falhas temporarias.

No `account-service`, a idempotencia usa a tabela `processed_transfer_events`. A chave e `transactionId`. Antes de debitar e creditar, o servico verifica se aquela transferencia ja foi processada:

```text
transactionId ja existe:
  nao movimenta saldo novamente
  republica o resultado conhecido

transactionId nao existe:
  processa debito/credito
  registra COMPLETED ou FAILED
  publica o resultado
```

Falhas de regra de negocio viram resultado financeiro `FAILED`, por exemplo:

```text
Conta nao encontrada
Conta inativa
Saldo insuficiente
```

Falhas tecnicas entram no mecanismo de resiliencia Kafka:

```text
erro tecnico no consumer
  retry configurado
  se continuar falhando, envia para <topico>.dlq
```

Configuracoes atuais:

```text
KAFKA_RETRY_MAX_ATTEMPTS=3
KAFKA_RETRY_INTERVAL_MS=1000
```

No `transaction-service`, eventos duplicados de resultado sao tratados de forma idempotente. Se a transacao ja estiver `COMPLETED` ou `FAILED`, o evento duplicado nao altera novamente o estado.

## Padrao Outbox

Os servicos que alteram banco e precisam publicar eventos Kafka usam o padrao Outbox.

No `transaction-service`, a criacao da transferencia e a gravacao do evento `transaction.transfer.requested` acontecem na mesma transacao de banco:

```text
transactions
outbox_events
```

No `account-service`, a movimentacao financeira, o registro de idempotencia e a gravacao do resultado tambem acontecem na mesma transacao de banco:

```text
accounts
processed_transfer_events
outbox_events
```

Um publicador agendado le eventos pendentes da `outbox_events`, publica no Kafka e marca como `PUBLISHED`.

Estados da Outbox:

```text
PENDING
PUBLISHED
FAILED
```

Configuracoes atuais:

```text
OUTBOX_PUBLISH_INTERVAL_MS=1000
OUTBOX_MAX_ATTEMPTS=5
```

Esse desenho evita perder eventos quando o banco confirma a operacao, mas o Kafka fica temporariamente indisponivel. O evento permanece salvo como `PENDING` e sera publicado quando o publicador conseguir enviar.

## Observabilidade Operacional

O gateway propaga o header `X-Correlation-Id`. Se o cliente nao enviar esse header, o gateway gera um UUID e repassa para os servicos internos. `account-service` e `transaction-service` incluem o valor no MDC dos logs.

Endpoints operacionais:

```text
GET /api/v1/operations/transactions/summary
  resumo de transacoes pendentes/falhadas, Outbox pendente/falhada e DLQs de resultado

GET /api/v1/operations/transactions/pending
  lista as 20 transacoes PENDING mais antigas

GET /api/v1/operations/transactions/outbox/failed
  lista os 20 eventos Outbox FAILED mais antigos do transaction-service

GET /api/v1/operations/accounts/summary
  resumo de transferencias processadas/falhadas, Outbox pendente/falhada e DLQ de requested

GET /api/v1/operations/accounts/outbox/failed
  lista os 20 eventos Outbox FAILED mais antigos do account-service
```

As mensagens em DLQ sao contadas por diferenca entre offsets iniciais e finais dos topicos:

```text
transaction.transfer.requested.dlq
transaction.transfer.completed.dlq
transaction.transfer.failed.dlq
```

## Autor

Ailton Martins  
Backend Developer (Java | Spring Boot | Microsservicos)

LinkedIn: https://www.linkedin.com/in/ailton-martins-1a4277136  
GitHub: https://github.com/ailtonmartins
