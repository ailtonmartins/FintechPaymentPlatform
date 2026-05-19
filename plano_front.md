# Plano Frontend - Fintech Payment Platform

## Objetivo

Criar um frontend simples em React com TypeScript para consumir os endpoints expostos pelo `api-gateway`, centralizando os fluxos principais da plataforma fintech em uma interface funcional e executavel via Docker.

O frontend deve ser simples, mas completo o suficiente para demonstrar:

- Cadastro, login e refresh token.
- Consulta do usuario autenticado.
- Criacao e consulta de contas.
- Operacoes de credito e debito.
- Criacao e consulta de transferencias.
- Visualizacao operacional de transacoes, contas, Outbox e DLQ.
- Consumo exclusivo pelo endpoint publico do API Gateway.

## Premissas

- O `api-gateway` e o ponto unico de entrada publica, rodando em `http://localhost:8080`.
- O frontend nao deve chamar diretamente `user-service`, `account-service` ou `transaction-service`.
- O access token JWT deve ser enviado no header:

```http
Authorization: Bearer <access_token>
```

- O refresh token deve ser usado para renovar a sessao quando necessario.
- Todas as mensagens exibidas ao usuario devem estar em portugues.
- O frontend deve rodar via Docker Compose junto com os demais servicos.

## Stack Proposta

- React
- TypeScript
- Vite
- React Router
- Axios
- React Hook Form
- Zod
- TanStack Query
- Docker
- Nginx para servir o build estatico em container

## Estrutura Sugerida

```text
frontend/
|--- Dockerfile
|--- nginx.conf
|--- package.json
|--- tsconfig.json
|--- vite.config.ts
|--- index.html
`--- src/
    |--- main.tsx
    |--- App.tsx
    |--- routes/
    |--- api/
    |--- auth/
    |--- components/
    |--- pages/
    |--- schemas/
    |--- types/
    `--- styles/
```

## Rotas Da Aplicacao

### Rotas Publicas

```text
/login
/register
```

### Rotas Protegidas

```text
/dashboard
/me
/accounts
/accounts/me
/transactions
/transactions/:id
/operations
/operations/transactions
/operations/accounts
```

## Telas

### 1. Login

Objetivo: autenticar usuario.

Endpoint:

```text
POST /api/v1/auth/login
```

Comportamento:

- Enviar email e senha.
- Armazenar access token e refresh token.
- Redirecionar para `/dashboard`.
- Exibir erros retornados pela API em portugues.

### 2. Cadastro

Objetivo: cadastrar novo usuario.

Endpoint:

```text
POST /api/v1/auth/register
```

Comportamento:

- Enviar dados de cadastro.
- Redirecionar para login apos sucesso.
- Validar campos no cliente antes do envio.

### 3. Usuario Autenticado

Objetivo: exibir dados do usuario logado.

Endpoint:

```text
GET /api/v1/me
```

Comportamento:

- Consultar dados do usuario autenticado.
- Exibir identificador, nome, email e roles, conforme resposta disponivel.

### 4. Usuarios Administrativos

Objetivo: permitir consulta administrativa quando o usuario tiver permissao.

Endpoints:

```text
GET /api/v1/users/{id}
GET /api/v1/users?email=
```

Comportamento:

- Criar formulario de busca por id.
- Criar formulario de busca por email.
- Exibir erro de permissao quando a API retornar `403`.

### 5. Contas

Objetivo: criar e consultar contas digitais.

Endpoints:

```text
POST /api/v1/accounts
GET  /api/v1/accounts/me
GET  /api/v1/accounts/{id}
```

Comportamento:

- Criar conta para o usuario autenticado.
- Exibir a propria conta.
- Buscar conta por id.
- Mostrar saldo, status e identificadores.

### 6. Operacoes De Saldo

Objetivo: executar credito e debito em uma conta.

Endpoints:

```text
POST /api/v1/accounts/{id}/credit
POST /api/v1/accounts/{id}/debit
```

Comportamento:

- Formulario com accountId e valor.
- Atualizar os dados da conta apos operacao bem-sucedida.
- Exibir erros de saldo insuficiente, conta inativa ou conta inexistente.

### 7. Transferencias

Objetivo: solicitar e consultar transferencias.

Endpoints:

```text
POST /api/v1/transactions/transfers
GET  /api/v1/transactions/{id}
```

Comportamento:

- Formulario com conta origem, conta destino e valor.
- Criar transferencia com status inicial `PENDING`.
- Permitir consulta por transactionId.
- Exibir status `PENDING`, `COMPLETED` ou `FAILED`.

### 8. Operacoes E Observabilidade

Objetivo: exibir informacoes operacionais dos fluxos financeiros.

Endpoints:

```text
GET /api/v1/operations/transactions/summary
GET /api/v1/operations/transactions/pending
GET /api/v1/operations/transactions/outbox/failed

GET /api/v1/operations/accounts/summary
GET /api/v1/operations/accounts/outbox/failed
```

Comportamento:

- Criar pagina `/operations` com abas para transacoes e contas.
- Exibir cards compactos de resumo operacional.
- Listar transacoes pendentes.
- Listar eventos Outbox com status `FAILED`.
- Exibir mensagens de vazio quando nao houver dados.

### 9. Metricas

Objetivo: consultar metricas expostas via gateway.

Endpoints:

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

Comportamento:

- Criar secao simples em `/operations` para metricas.
- Exibir nome da metrica, descricao e valores disponiveis.
- Tratar indisponibilidade de actuator sem quebrar a tela.

## Camada De API

Criar um cliente HTTP centralizado:

```text
src/api/httpClient.ts
```

Responsabilidades:

- Definir `baseURL` usando variavel de ambiente `VITE_API_BASE_URL`.
- Adicionar access token no header `Authorization`.
- Interceptar respostas `401`.
- Tentar refresh token uma vez.
- Encerrar sessao se o refresh falhar.

Variavel local:

```text
VITE_API_BASE_URL=http://localhost:8080
```

Variavel no Docker Compose:

```text
VITE_API_BASE_URL=http://localhost:8080
```

## Autenticacao No Frontend

Criar modulo:

```text
src/auth/
|--- AuthProvider.tsx
|--- authStorage.ts
`--- ProtectedRoute.tsx
```

Responsabilidades:

- Manter estado de autenticacao.
- Persistir tokens.
- Expor login, logout e refresh.
- Proteger rotas privadas.
- Redirecionar usuarios nao autenticados para `/login`.

## Componentes Principais

```text
components/
|--- AppLayout.tsx
|--- Sidebar.tsx
|--- Topbar.tsx
|--- FormField.tsx
|--- LoadingState.tsx
|--- ErrorState.tsx
|--- EmptyState.tsx
|--- StatusBadge.tsx
`--- DataTable.tsx
```

## UX Simples

- Layout de aplicacao operacional, sem landing page.
- Menu lateral com acesso rapido aos modulos.
- Feedback visual para loading, erro e sucesso.
- Formularios objetivos.
- Tabelas simples para dados operacionais.
- Badges para status financeiros.
- Interface responsiva para desktop e mobile.

## Docker

Criar `frontend/Dockerfile` com build multi-stage:

```text
1. Usar imagem Node para instalar dependencias e gerar build.
2. Usar Nginx para servir arquivos estaticos.
3. Configurar fallback para SPA.
```

Criar `frontend/nginx.conf`:

```text
- Servir `/usr/share/nginx/html`.
- Redirecionar rotas desconhecidas para `index.html`.
```

Atualizar `docker-compose.yaml`:

```text
frontend:
  build:
    context: ./frontend
  container_name: fintech-frontend
  ports:
    - "3000:80"
  depends_on:
    - api-gateway
```

URL do frontend:

```text
http://localhost:3000
```

## Ordem De Implementacao

### 1. Scaffold

- Criar projeto React com Vite e TypeScript.
- Configurar ESLint se o projeto ainda nao tiver padrao definido.
- Criar estrutura de pastas.
- Configurar React Router.

### 2. Cliente HTTP

- Criar `httpClient`.
- Configurar `VITE_API_BASE_URL`.
- Criar interceptors de token e refresh.
- Padronizar tratamento de erros.

### 3. Autenticacao

- Implementar cadastro.
- Implementar login.
- Implementar logout.
- Implementar rotas protegidas.
- Implementar consulta de `/api/v1/me`.

### 4. Contas

- Criar tela de conta do usuario.
- Criar conta.
- Buscar conta por id.
- Executar credito.
- Executar debito.

### 5. Transferencias

- Criar transferencia.
- Consultar transferencia por id.
- Exibir status e detalhes.

### 6. Operacional

- Criar resumo de transacoes.
- Criar lista de transacoes pendentes.
- Criar lista de Outbox FAILED de transacoes.
- Criar resumo de contas.
- Criar lista de Outbox FAILED de contas.
- Criar visualizacao basica de metricas.

### 7. Docker

- Criar Dockerfile.
- Criar configuracao Nginx.
- Adicionar servico `frontend` no Compose.
- Validar acesso via `http://localhost:3000`.

### 8. Validacao Manual

- Subir ambiente com `docker compose up -d`.
- Cadastrar usuario.
- Fazer login.
- Criar conta.
- Creditar saldo.
- Criar segunda conta ou usar outra conta existente.
- Solicitar transferencia.
- Consultar status da transferencia.
- Verificar telas operacionais.

## Criterios De Aceite

- O frontend roda em Docker na porta `3000`.
- Todas as chamadas passam pelo `api-gateway`.
- Login e refresh token funcionam.
- Rotas protegidas bloqueiam usuario nao autenticado.
- Todos os endpoints principais documentados no README possuem tela ou acao no frontend.
- Erros da API sao exibidos em portugues.
- A interface permite demonstrar o fluxo completo: cadastro, login, conta, saldo, transferencia e consulta operacional.

## Riscos E Cuidados

- Confirmar o formato real dos DTOs de request e response antes de implementar formularios finais.
- Verificar se endpoints de metricas estao liberados pelo gateway.
- Evitar acoplamento do frontend aos servicos internos.
- Tratar `403` nos endpoints administrativos sem quebrar a navegacao.
- Garantir que o refresh token nao entre em loop em caso de falha.

## Proxima Acao

Antes de implementar o frontend, mapear os DTOs reais nos controllers dos servicos e criar os tipos TypeScript correspondentes em `src/types/`.
