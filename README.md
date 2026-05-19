# City Transporte — Sistema de Gestão de Terminal

Sistema web para controle operacional de terminais de carga e descarga: cadastros, fluxo de check-in / execução / checkout, registro de incidentes e dashboard de acompanhamento.

Projeto acadêmico — back-end em Spring Boot 3.3 + front-end em React 18 (Vite), banco PostgreSQL.

---

## Estrutura

```
controle_terminal_onibus/
├── backend/          # Spring Boot 3.3.5 + Java 17 (Maven)
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/java/com/controle/terminal/...
│       ├── main/resources/
│       │   ├── application.properties
│       │   └── schema.sql        # DDL + seeds de demonstração
│       └── test/java/com/controle/terminal/service/   # testes unitários
└── frontend/         # React 18 + Vite + TypeScript + Tailwind
    ├── package.json
    └── src/
```

---

## Pré-requisitos

- **Java 17** (`java -version`)
- **Maven** — opcional, o wrapper `mvnw` já está incluído
- **PostgreSQL 14+** rodando localmente
- **Node.js 18+** e **npm**

---

## Setup do banco de dados

1. Crie o banco no PostgreSQL (uma única vez):

   ```sql
   CREATE DATABASE terminal;
   ```

2. Rode o script de schema:

   ```bash
   psql -U postgres -d terminal -f backend/src/main/resources/schema.sql
   ```

   O script cria as 10 tabelas, restrições e *seeds* de demonstração: 2 terminais, 8 docas, 1 estacionamento + 4 vagas, 5 veículos e 5 tipos de incidente. **Não cria os usuários** — eles são inseridos automaticamente pelo back-end no primeiro start (ver abaixo).

---

## Executando o back-end

1. Configure as variáveis de ambiente do banco (PowerShell):

   ```powershell
   $env:DB_HOST = "localhost"
   $env:DB_USER = "postgres"
   $env:DB_PASSWORD = "postgres"
   ```

   Em bash/zsh:

   ```bash
   export DB_HOST=localhost
   export DB_USER=postgres
   export DB_PASSWORD=postgres
   ```

2. Suba a aplicação:

   ```bash
   cd backend
   ./mvnw spring-boot:run        # Linux/Mac
   ./mvnw.cmd spring-boot:run    # Windows
   ```

   A API sobe em `http://localhost:8080`. No primeiro start, o `DataInitializer` insere os 4 usuários default (se a tabela `usuario` estiver vazia) com senhas BCrypt.

3. Rodar os testes unitários (70 testes cobrindo todos os services):

   ```bash
   ./mvnw test
   ```

---

## Executando o front-end

```bash
cd frontend
npm install
npm run dev
```

A interface sobe em `http://localhost:5173` e consome a API em `http://localhost:8080` (configurado em `vite.config.ts`).

---

## Credenciais de acesso (seed)

| Email                | Senha     | Perfil         |
|----------------------|-----------|----------------|
| admin@city.com       | admin123  | ADMINISTRADOR  |
| supervisor@city.com  | sup123    | SUPERVISOR     |
| operador@city.com    | oper123   | OPERADOR       |
| seguranca@city.com   | seg123    | SEGURANCA      |

---

## Perfis e permissões (RBAC)

| Ação                                      | ADMIN | SUPERVISOR | OPERADOR | SEGURANCA |
|-------------------------------------------|:-----:|:----------:|:--------:|:---------:|
| Login + visualizar todas as telas         | ✅    | ✅         | ✅       | ✅        |
| CRUD de terminais / tipos de incidente / usuários | ✅ | ❌      | ❌       | ❌        |
| CRUD de docas / estacionamentos / vagas   | ✅    | ✅         | ❌       | ❌        |
| Alterar status de doca/vaga               | ✅    | ✅         | ✅       | ❌        |
| CRUD de veículos                          | ✅    | ✅         | ✅       | ❌        |
| Check-in / checkout de operação           | ✅    | ❌         | ✅       | ✅        |
| Iniciar / finalizar operação              | ✅    | ✅         | ✅       | ❌        |
| Cancelar operação                         | ✅    | ✅         | ❌       | ❌        |
| Registrar incidente                       | ✅    | ✅         | ✅       | ✅        |
| Encerrar incidente                        | ✅    | ✅         | ❌       | ❌        |

---

## Fluxo operacional

```
       check-in              iniciar              finalizar             checkout
AGENDADA ──────► EM_ANDAMENTO ──────► FINALIZADA ──────► (recurso liberado)
   │                                       
   └─► cancelar ─► CANCELADA (libera doca/vaga)
```

- **Check-in** aloca uma doca (`DISPONIVEL → OCUPADA`) **ou** uma vaga (`LIVRE → OCUPADA`) — nunca os dois.
- **Iniciar** exige que a operação tenha doca ou vaga atribuída.
- **Finalizar** registra `dt_fim`; a doca/vaga continua ocupada.
- **Checkout** libera a doca/vaga e é idempotente.
- **Cancelar** é bloqueado se a operação já estiver `FINALIZADA`.

Transições inválidas devolvem `409 Conflict` com `code=INVALID_STATE_TRANSITION` e mensagem descritiva.

---

## Principais endpoints

> Todos exigem header `Authorization: Bearer <token>` exceto `POST /api/auth/login`.

### Autenticação

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@city.com","senha":"admin123"}'
```

Resposta:
```json
{
  "accessToken": "eyJhbGciOi...",
  "tokenType": "Bearer",
  "expiresInMs": 86400000,
  "usuario": { "id": 1, "nome": "Administrador", "email": "admin@city.com", "perfil": "ADMINISTRADOR" }
}
```

### Operações

```bash
# Check-in (aloca doca disponível para um veículo)
curl -X POST http://localhost:8080/api/operacoes/checkin \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"terminalId":1,"veiculoId":1,"tipo":"CARGA","docaId":1,"descricaoCarga":"Eletrônicos"}'

# Iniciar / finalizar / checkout / cancelar
curl -X PATCH http://localhost:8080/api/operacoes/1/iniciar    -H "Authorization: Bearer $TOKEN"
curl -X PATCH http://localhost:8080/api/operacoes/1/finalizar  -H "Authorization: Bearer $TOKEN"
curl -X PATCH http://localhost:8080/api/operacoes/1/checkout   -H "Authorization: Bearer $TOKEN"
curl -X PATCH http://localhost:8080/api/operacoes/1/cancelar   -H "Authorization: Bearer $TOKEN"

# Listar com filtros
curl "http://localhost:8080/api/operacoes?status=EM_ANDAMENTO&terminalId=1&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### Documentos de carga

```bash
curl -X POST http://localhost:8080/api/operacoes/1/documentos \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"tipo":"NOTA_FISCAL","numero":"NF-12345","emitidoEm":"2026-05-18"}'
```

### Incidentes

```bash
# Registrar
curl -X POST http://localhost:8080/api/incidentes \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{
        "tipoIncidenteId":1, "terminalId":1, "docaId":2,
        "ocorridoEm":"2026-05-18T14:30:00",
        "descricao":"Vazamento detectado na doca D-02"
      }'

# Encerrar
curl -X PATCH http://localhost:8080/api/incidentes/1/encerrar \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"acaoTomada":"Área isolada e limpa pela equipe de manutenção"}'
```

### Dashboard

```bash
curl "http://localhost:8080/api/dashboard/resumo?terminalId=1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Formato padrão de erro

Toda falha (4xx/5xx) devolve o mesmo payload, facilitando o tratamento no front-end:

```json
{
  "timestamp": "2026-05-18T22:32:11.482-03:00",
  "status": 409,
  "error": "Conflict",
  "code": "DOCA_UNAVAILABLE",
  "message": "A doca 'D-03' nao esta disponivel. Status atual: OCUPADA.",
  "path": "/api/operacoes/checkin",
  "details": null,
  "traceId": "9b3a7c0e"
}
```

Códigos semânticos principais: `RESOURCE_NOT_FOUND`, `DUPLICATE_*`, `DOCA_UNAVAILABLE`, `VAGA_UNAVAILABLE`, `OPERACAO_NOT_FINISHED`, `INVALID_STATE_TRANSITION`, `INCIDENT_ALREADY_CLOSED`, `VALIDATION_FAILED`, `INVALID_CREDENTIALS`, `FORBIDDEN`.

---

## Stack técnica

**Back-end**
- Spring Boot 3.3.5 (Web, Data JPA, Security, Validation)
- Java 17 + Lombok
- jjwt 0.12.6 (geração/validação de JWT HS256)
- PostgreSQL driver
- JUnit 5 + Mockito + AssertJ (70 testes unitários cobrindo todos os services)

**Front-end**
- React 18 + Vite + TypeScript
- Tailwind CSS
- TanStack Query 5 (cache + polling do dashboard a cada 15s)
- React Hook Form + Zod (validação de formulários)
- React Router 6
- Axios (com interceptor para JWT e redirect em 401)
- Recharts

**Banco**
- PostgreSQL 14+
- Schema gerenciado manualmente via `schema.sql` 
- `spring.jpa.hibernate.ddl-auto=validate` apenas valida o mapeamento contra o schema criado pelo `.sql`

---

## Variáveis de configuração

Em `backend/src/main/resources/application.properties`:

| Propriedade               | Padrão / Origem        | Descrição                                  |
|---------------------------|------------------------|--------------------------------------------|
| `DB_HOST`                 | env var                | host do PostgreSQL                         |
| `DB_USER`                 | env var                | usuário do PostgreSQL                      |
| `DB_PASSWORD`             | env var                | senha do PostgreSQL                        |
| `app.jwt.secret`          | string base64 (≥32B)   | chave HMAC do JWT                          |
| `app.jwt.expiration-ms`   | 86400000 (24h)         | tempo de vida do access token              |
| `app.cors.allowed-origins`| `:5173,:3000`          | origens permitidas pelo CORS               |

---

## Verificação ponta a ponta

1. Banco criado e `schema.sql` 
2. `./mvnw spring-boot:run` no `backend/` sobe na porta 8080
3. `npm run dev` no `frontend/` sobe na porta 5173
4. Login em `http://localhost:5173/login` com `admin@city.com / admin123`
5. Dashboard mostra docas com cores por status (verde = disponível, amarelo = ocupada, vermelho = interditada)
6. Criar veículo → check-in em doca disponível → iniciar → finalizar → checkout (no fim, conferir no Postgres: `SELECT status_doca FROM doca WHERE cd_doca='D-01'` volta a `DISPONIVEL`)
7. Tentar check-in em doca já ocupada → API responde 409 com `code=DOCA_UNAVAILABLE`
8. Logar como OPERADOR → menu "Usuários" some e `/usuarios` redireciona; `POST /api/usuarios` devolve 403 com `code=FORBIDDEN`
