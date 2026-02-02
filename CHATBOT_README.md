# 🤖 Chatbot Musa Spa - Documentação do Fluxo

## Visão Geral

O chatbot implementa um sistema completo de atendimento baseado em menus/botões para o Musa Spa, permitindo:

- ✅ Navegação por menus hierárquicos
- ✅ Visualização de serviços e preços
- ✅ Agendamento completo com busca de datas no banco
- ✅ Coleta de dados do cliente (nome, CPF, telefone, email)
- ✅ Criação automática de cliente se não existir (busca por CPF)
- ✅ Suporte técnico com geração de tickets
- ✅ Transferência para atendente humano

## Arquitetura

### Arquivos Criados/Modificados

| Arquivo | Descrição |
|---------|-----------|
| `entity/ChatSession.java` | Entidade que armazena o estado da sessão e dados temporários |
| `repository/ChatSessionRepository.java` | Repositório para persistência das sessões |
| `service/ChatFlowService.java` | **Coração do chatbot** - toda lógica de fluxo |
| `service/MessageHandlerService.java` | Atualizado para delegar ao ChatFlowService |
| `entity/Client.java` | Adicionado campo CPF |
| `repository/ClientRepository.java` | Adicionado método findByCpf |
| `schema.sql` | Atualizado com tabela chat_sessions e novos serviços |
| `migration_chatbot.sql` | Script de migração para bancos existentes |

## Estrutura do Fluxo

```
NÍVEL 0 - Menu Principal
├── 1 - Ver Serviços (NÍVEL 1)
│   ├── 1 - Faciais → Lista serviços → Detalhes → Agendar
│   ├── 2 - Manicure → Lista serviços → Detalhes → Agendar
│   ├── 3 - Corporais → Lista serviços → Detalhes → Agendar
│   ├── 4 - Cabelo → Lista serviços → Detalhes → Agendar
│   └── 5 - Pacotes → Lista serviços → Detalhes → Agendar
│
├── 2 - Consultar Preços (NÍVEL 2)
│   ├── 1 - Faciais → Tabela de preços → Agendar
│   ├── 2 - Manicure → Tabela de preços → Agendar
│   ├── 3 - Corporais → Tabela de preços → Agendar
│   ├── 4 - Cabelo → Tabela de preços → Agendar
│   └── 5 - Pacotes → Tabela de preços → Agendar
│
├── 3 - Agendar Horário (NÍVEL 3)
│   └── Categoria → Serviço → Data → Horário → Dados → Confirmação
│
├── 4 - Suporte Técnico (NÍVEL 4)
│   ├── 1 - Não consigo agendar → Detalhes → Coleta dados → Ticket
│   ├── 2 - Remarcar horário → Coleta dados → Ticket
│   ├── 3 - Cancelar → Coleta dados → Ticket
│   ├── 4 - Dúvidas pagamento → Coleta dados → Ticket
│   ├── 5 - Cupom/voucher → Coleta dados → Ticket
│   ├── 6 - Problemas confirmação → Coleta dados → Ticket
│   └── 7 - Outro problema → Coleta dados → Ticket
│
└── 5 - Falar com Atendente (NÍVEL 5)
    └── Coleta dados → Mensagem → Fila de atendimento
```

## Comandos Globais

- `menu` ou `0` (no menu principal) - Volta ao menu principal
- `0` (em qualquer outro nível) - Volta ao menu anterior

## Estados da Sessão (MenuLevel)

```java
public enum MenuLevel {
    MAIN_MENU,              // Menu principal
    
    // Serviços
    SERVICES_MENU,          // Menu de categorias
    SERVICES_FACIAL,        // Lista de serviços faciais
    SERVICES_MANICURE,      // Lista de manicure
    SERVICES_CORPORAL,      // Lista de corporais
    SERVICES_CABELO,        // Lista de cabelo
    SERVICES_PACOTES,       // Lista de pacotes
    
    // Preços
    PRICES_MENU,            // Menu de categorias
    PRICES_FACIAL,          // Preços faciais
    PRICES_MANICURE,        // Preços manicure
    PRICES_CORPORAL,        // Preços corporais
    PRICES_CABELO,          // Preços cabelo
    PRICES_PACOTES,         // Preços pacotes
    
    // Agendamento
    SCHEDULING_CATEGORY,    // Escolha categoria
    SCHEDULING_SERVICE,     // Escolha serviço
    SCHEDULING_DATE,        // Escolha data
    SCHEDULING_TIME,        // Escolha horário
    SCHEDULING_NAME,        // Coleta nome
    SCHEDULING_CPF,         // Coleta CPF
    SCHEDULING_PHONE,       // Coleta telefone
    SCHEDULING_EMAIL,       // Coleta email
    SCHEDULING_CONFIRMATION,// Confirmação final
    
    // Suporte
    SUPPORT_MENU,           // Menu de problemas
    SUPPORT_PROBLEM,        // Detalhes do problema
    SUPPORT_NAME,           // Coleta nome
    SUPPORT_CPF,            // Coleta CPF
    SUPPORT_PHONE,          // Coleta telefone
    SUPPORT_EMAIL,          // Coleta email
    
    // Atendente
    ATTENDANT_NAME,         // Coleta nome
    ATTENDANT_CPF,          // Coleta CPF
    ATTENDANT_PHONE,        // Coleta telefone
    ATTENDANT_EMAIL,        // Coleta email
    ATTENDANT_MESSAGE,      // Mensagem para atendente
    ATTENDANT_QUEUE,        // Aguardando na fila
    
    WAITING_HUMAN           // Aguardando atendente humano
}
```

## Categorias de Serviços

As categorias são mapeadas conforme abaixo:

| Código | Categoria | Emoji |
|--------|-----------|-------|
| 1 | Facial | 💆‍♀️ |
| 2 | Manicure | 💅 |
| 3 | Corporal | 🧖‍♀️ |
| 4 | Cabelo | 💇‍♀️ |
| 5 | Pacotes | 👰 |

## Lógica de Agendamento

### Datas Disponíveis
- Busca os próximos 6 dias úteis (exceto domingo)
- Iniciando a partir do dia seguinte

### Horários Disponíveis
- Horário de funcionamento: 9h às 18h
- Horários fixos: 09:00, 10:00, 11:00, 13:00, 14:00, 15:00, 16:00, 17:00
- Filtra horários já ocupados consultando a tabela `appointments`

### Criação de Cliente
1. Busca cliente por CPF (`clientRepository.findByCpf`)
2. Se não existir, cria novo cliente
3. Atualiza dados do cliente (nome, telefone, email)

### Criação de Agendamento
- Status inicial: `PENDENTE`
- Sala padrão: `Sala 1` (pode ser customizado)

## Como Testar

1. **Inicie a aplicação:**
```bash
./mvnw spring-boot:run
```

2. **Execute o script de migração** (se já tem banco):
```bash
mysql -u root -p musa < src/main/resources/migration_chatbot.sql
```

3. **Envie uma mensagem** para o número conectado na Evolution API

4. **Observe os logs** para acompanhar o fluxo:
```
📩 Processando mensagem de 5511999999999 | Mensagem: 1
📱 Processando: 5511999999999 | Nível: MAIN_MENU | Mensagem: 1
```

## Personalizações

### Alterar Horários de Funcionamento

No arquivo `ChatFlowService.java`, método `getAvailableTimes`:

```java
List<LocalTime> allTimes = Arrays.asList(
    LocalTime.of(9, 0),   // Primeiro horário
    LocalTime.of(10, 0),
    // ... adicione/remova horários
    LocalTime.of(18, 0)   // Último horário
);
```

### Alterar Dias de Funcionamento

No método `getAvailableDates`:

```java
// Excluir domingo e segunda:
if (date.getDayOfWeek() != DayOfWeek.SUNDAY && 
    date.getDayOfWeek() != DayOfWeek.MONDAY) {
    dates.add(date);
}
```

### Alterar Mensagens

Todas as mensagens estão em strings formatadas (text blocks) no `ChatFlowService.java`. 
Basta alterar o texto mantendo a formatação WhatsApp:
- `*texto*` = negrito
- `_texto_` = itálico
- `~texto~` = tachado

## Banco de Dados

### Tabela chat_sessions

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT | PK auto-increment |
| phone_number | VARCHAR(50) | Número do WhatsApp (único) |
| menu_level | VARCHAR(50) | Estado atual do menu |
| flow_type | VARCHAR(30) | Tipo de fluxo ativo |
| selected_category | VARCHAR(50) | Categoria selecionada |
| selected_service_id | BIGINT | FK para services |
| selected_date | DATE | Data selecionada |
| selected_time | TIME | Horário selecionado |
| temp_name | VARCHAR(255) | Nome temporário (coleta) |
| temp_cpf | VARCHAR(11) | CPF temporário |
| temp_phone | VARCHAR(20) | Telefone temporário |
| temp_email | VARCHAR(255) | Email temporário |
| temp_message | TEXT | Mensagem para suporte/atendente |
| ticket_number | VARCHAR(20) | Número do ticket |
| previous_level | VARCHAR(50) | Nível anterior (para voltar) |
| last_interaction | DATETIME | Última interação |
| created_at | DATETIME | Data de criação |

### Alteração na tabela clients

Adicionado campo `cpf VARCHAR(11) UNIQUE` para identificação única do cliente.

---

**Musa Spa** - Onde sua beleza floresce ✨
