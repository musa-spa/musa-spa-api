# Musa SPA API

API REST para gerenciamento do Musa SPA - Sistema completo de agendamentos, clientes, serviços, campanhas e integração com WhatsApp.

## 🚀 Tecnologias

- **Java 17+**
- **Spring Boot 3.5.9**
- **Spring Data JPA**
- **MySQL**
- **Swagger/OpenAPI 3** (springdoc-openapi)
- **Lombok**

## 📋 Pré-requisitos

- JDK 17 ou superior
- Maven 3.8+
- MySQL 8.0+
- Docker (opcional, para o banco de dados)

## 🔧 Configuração

### Variáveis de Ambiente

```bash
DB_HOST=localhost      # Host do banco de dados
DB_PORT=3307          # Porta do banco de dados
DB_NAME=musa          # Nome do banco de dados
DB_USER=root          # Usuário do banco
DB_PASS=verysecret    # Senha do banco
```

### Executando com Docker

```bash
docker-compose up -d
```

### Executando localmente

```bash
./mvnw spring-boot:run
```

## 📖 Documentação da API (Swagger)

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/api-docs

## 🔗 Endpoints Disponíveis

### 🔐 Autenticação (`/api/auth`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Realizar login |
| POST | `/api/auth/request-access` | Solicitar acesso |
| GET | `/api/auth/me` | Dados do usuário logado |

**Credenciais de teste:**
- Email: `admin@musasp.com.br`
- Senha: `admin123`

### 📊 Dashboard (`/api/dashboard`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/dashboard/stats` | Estatísticas gerais |
| GET | `/api/dashboard/upcoming-appointments` | Próximos agendamentos |

### 👥 Clientes (`/api/clients`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/clients` | Listar todos os clientes |
| GET | `/api/clients/{id}` | Buscar cliente por ID |
| GET | `/api/clients/search?term=` | Buscar clientes |
| POST | `/api/clients` | Criar novo cliente |
| PUT | `/api/clients/{id}` | Atualizar cliente |
| DELETE | `/api/clients/{id}` | Excluir cliente |

### 💆 Serviços (`/api/services`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/services` | Listar todos os serviços |
| GET | `/api/services/active` | Listar serviços ativos |
| GET | `/api/services/{id}` | Buscar serviço por ID |
| GET | `/api/services/category/{category}` | Listar por categoria |
| GET | `/api/services/search?term=` | Buscar serviços |
| POST | `/api/services` | Criar novo serviço |
| PUT | `/api/services/{id}` | Atualizar serviço |
| PATCH | `/api/services/{id}/toggle-active` | Ativar/Desativar serviço |
| DELETE | `/api/services/{id}` | Excluir serviço |

### 📅 Agendamentos (`/api/appointments`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/appointments` | Listar todos os agendamentos |
| GET | `/api/appointments/{id}` | Buscar agendamento por ID |
| GET | `/api/appointments/date/{date}` | Listar por data (YYYY-MM-DD) |
| GET | `/api/appointments/range?startDate=&endDate=` | Listar por período |
| GET | `/api/appointments/client/{clientId}` | Listar por cliente |
| POST | `/api/appointments` | Criar novo agendamento |
| PUT | `/api/appointments/{id}` | Atualizar agendamento |
| PATCH | `/api/appointments/{id}/status` | Atualizar status |
| DELETE | `/api/appointments/{id}` | Excluir agendamento |

### 📢 Campanhas (`/api/campaigns`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/campaigns` | Listar todas as campanhas |
| GET | `/api/campaigns/{id}` | Buscar campanha por ID |
| GET | `/api/campaigns/status/{status}` | Listar por status |
| POST | `/api/campaigns` | Criar nova campanha |
| PUT | `/api/campaigns/{id}` | Atualizar campanha |
| POST | `/api/campaigns/{id}/send` | Enviar campanha agora |
| DELETE | `/api/campaigns/{id}` | Excluir campanha |

### 💬 WhatsApp (`/api/whatsapp`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/whatsapp/kpis` | KPIs do WhatsApp |
| GET | `/api/whatsapp/contacts` | Contatos ativos |
| GET | `/api/whatsapp/contacts/all` | Todos os contatos |
| GET | `/api/whatsapp/status` | Status da integração |

### ⚙️ Configurações (`/api/configurations`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/configurations` | Obter todas as configurações |
| POST | `/api/configurations` | Salvar configurações |

### 🔧 Sistema (`/api`)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/health` | Health check |
| GET | `/api/categories` | Lista de categorias |
| GET | `/api/rooms` | Lista de salas |
| GET | `/api/time-slots` | Lista de horários |
| GET | `/api/target-audiences` | Públicos-alvo para campanhas |

## 📁 Estrutura do Projeto

```
src/main/java/spa/musa/send/mensage/
├── config/                 # Configurações (CORS, Swagger, etc.)
├── controller/             # Controllers REST
├── dto/                    # Data Transfer Objects
├── entity/                 # Entidades JPA
├── exception/              # Tratamento de exceções
├── repository/             # Repositórios JPA
├── service/                # Camada de serviços
├── evolution/              # Integração com API Evolution
└── webhook/                # Webhooks
```

## 🎨 Modelos de Dados

### Cliente
```json
{
  "id": 1,
  "name": "Maria Silva",
  "phone": "(11) 99999-1111",
  "email": "maria@email.com",
  "notes": "Cliente VIP",
  "lastVisit": "20/01/2026",
  "totalVisits": 12,
  "serviceHistory": []
}
```

### Serviço
```json
{
  "id": 1,
  "name": "Limpeza de Pele",
  "description": "Limpeza profunda da pele",
  "category": "Facial",
  "duration": 60,
  "price": 150.00,
  "active": true
}
```

### Agendamento
```json
{
  "id": 1,
  "date": "2026-01-25",
  "time": "09:00",
  "client": "Maria Silva",
  "clientId": 1,
  "service": "Limpeza de Pele",
  "serviceId": 1,
  "room": "Sala 1",
  "status": "confirmado"
}
```

### Campanha
```json
{
  "id": 1,
  "title": "Promoção de Verão",
  "message": "Aproveite 20% de desconto!",
  "status": "enviada",
  "targetAudience": "all",
  "sentDate": "20/01/2026",
  "recipients": 45,
  "opens": 38
}
```

## 🔒 Segurança

Por padrão, a API permite CORS de todas as origens para facilitar o desenvolvimento. Em produção, configure as origens permitidas adequadamente.

## 📞 Suporte

Para dúvidas ou problemas, entre em contato: contato@musasp.com.br
