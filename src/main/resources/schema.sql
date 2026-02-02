-- ============================================
-- Musa SPA - Script de Criação do Banco de Dados
-- MySQL 8.0+
-- ============================================

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS musa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE musa;

-- ============================================
-- TABELA: clients (Clientes)
-- ============================================
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(255),
    notes TEXT,
    last_visit DATETIME,
    total_visits INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_clients_phone (phone),
    INDEX idx_clients_email (email),
    INDEX idx_clients_name (name),
    INDEX idx_clients_cpf (cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: services (Serviços do SPA)
-- ============================================
CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    duration INT NOT NULL COMMENT 'Duração em minutos',
    price DECIMAL(10,2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_services_category (category),
    INDEX idx_services_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: appointments (Agendamentos)
-- ============================================
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    room VARCHAR(50) NOT NULL,
    status ENUM('PENDENTE', 'CONFIRMADO', 'CANCELADO', 'REALIZADO') DEFAULT 'PENDENTE',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    INDEX idx_appointments_date (appointment_date),
    INDEX idx_appointments_status (status),
    INDEX idx_appointments_client (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: service_history (Histórico de Serviços)
-- ============================================
CREATE TABLE IF NOT EXISTS service_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    service_date DATETIME NOT NULL,
    status ENUM('REALIZADO', 'CANCELADO') DEFAULT 'REALIZADO',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    INDEX idx_service_history_client (client_id),
    INDEX idx_service_history_date (service_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: campaigns (Campanhas de Marketing)
-- ============================================
CREATE TABLE IF NOT EXISTS campaigns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status ENUM('RASCUNHO', 'AGENDADA', 'ENVIADA') DEFAULT 'RASCUNHO',
    target_audience VARCHAR(50) NOT NULL,
    sent_date DATETIME,
    scheduled_date DATETIME,
    recipients INT DEFAULT 0,
    opens INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_campaigns_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: configurations (Configurações do Sistema)
-- ============================================
CREATE TABLE IF NOT EXISTS configurations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    config_group VARCHAR(50),
    description VARCHAR(255),
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_configurations_key (config_key),
    INDEX idx_configurations_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: whatsapp_contacts (Contatos WhatsApp)
-- ============================================
CREATE TABLE IF NOT EXISTS whatsapp_contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    status ENUM('ATIVO', 'HUMANO', 'AGUARDANDO', 'FINALIZADO') DEFAULT 'ATIVO',
    stage VARCHAR(100),
    last_message DATETIME,
    unread INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_whatsapp_contacts_status (status),
    INDEX idx_whatsapp_contacts_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: contacts (Contatos - já existente)
-- ============================================
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255),
    opted_in BOOLEAN DEFAULT TRUE,
    tags VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_interaction DATETIME,
    INDEX idx_contacts_number (number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: conversations (Conversas - já existente)
-- ============================================
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contact_id BIGINT,
    status VARCHAR(50),
    current_step VARCHAR(100),
    agent_name VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contact_id) REFERENCES contacts(id) ON DELETE SET NULL,
    INDEX idx_conversations_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: messages (Mensagens - já existente)
-- ============================================
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT,
    number VARCHAR(50),
    direction VARCHAR(20),
    content TEXT,
    message_type VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    INDEX idx_messages_conversation (conversation_id),
    INDEX idx_messages_number (number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: chat_sessions (Sessões de Chat do Bot)
-- ============================================
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(50) NOT NULL UNIQUE,
    menu_level VARCHAR(50) NOT NULL DEFAULT 'MAIN_MENU',
    flow_type VARCHAR(30) NOT NULL DEFAULT 'NONE',
    selected_category VARCHAR(50),
    selected_service_id BIGINT,
    selected_date DATE,
    selected_time TIME,
    temp_name VARCHAR(255),
    temp_cpf VARCHAR(11),
    temp_phone VARCHAR(20),
    temp_email VARCHAR(255),
    temp_message TEXT,
    ticket_number VARCHAR(20),
    previous_level VARCHAR(50),
    last_interaction DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (selected_service_id) REFERENCES services(id) ON DELETE SET NULL,
    INDEX idx_chat_sessions_phone (phone_number),
    INDEX idx_chat_sessions_level (menu_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DADOS INICIAIS
-- ============================================

-- Inserir serviços padrão
INSERT INTO services (name, description, category, duration, price, active) VALUES
-- Tratamentos Faciais
('Limpeza de Pele Profunda', 'Remoção de impurezas, extração de cravos, esfoliação e hidratação profunda. Indicado para todos os tipos de pele.', 'Facial', 60, 180.00, TRUE),
('Peeling Químico', 'Renovação celular com ácidos específicos. Trata manchas, linhas finas e textura irregular. Requer avaliação prévia.', 'Facial', 45, 250.00, TRUE),
('Hydrafacial', 'Tecnologia 3 em 1: limpeza, esfoliação e hidratação com infusão de séruns. Resultado imediato e sem downtime.', 'Facial', 90, 380.00, TRUE),
('Radiofrequência Facial', 'Estímulo de colágeno para firmeza e redução de flacidez. Efeito lifting natural.', 'Facial', 50, 220.00, TRUE),
-- Manicure e Pedicure
('Manicure Tradicional', 'Cuticulagem, lixamento e esmaltação profissional. Unhas perfeitas e bem cuidadas.', 'Manicure', 40, 45.00, TRUE),
('Pedicure Completa', 'Tratamento completo dos pés com esfoliação, hidratação e esmaltação.', 'Manicure', 50, 55.00, TRUE),
('Spa dos Pés', 'Tratamento premium com escalda-pés, esfoliação, massagem relaxante e esmaltação.', 'Manicure', 90, 120.00, TRUE),
('Alongamento em Gel', 'Unhas alongadas com gel de alta durabilidade. Incluí manutenção após 21 dias.', 'Manicure', 120, 180.00, TRUE),
-- Tratamentos Corporais
('Massagem Relaxante', 'Massagem corporal com óleos essenciais para relaxamento total e alívio do estresse.', 'Corporal', 60, 200.00, TRUE),
('Massagem Modeladora', 'Massagem intensiva para redução de medidas, combate à celulite e definição corporal.', 'Corporal', 50, 180.00, TRUE),
('Drenagem Linfática', 'Técnica manual para eliminar toxinas, reduzir inchaço e retenção de líquidos.', 'Corporal', 60, 170.00, TRUE),
('Esfoliação Corporal', 'Renovação da pele com esfoliantes naturais seguida de hidratação profunda.', 'Corporal', 45, 150.00, TRUE),
-- Cabelo e Estética Capilar
('Hidratação Capilar', 'Tratamento intensivo para recuperar brilho, maciez e vitalidade dos fios.', 'Cabelo', 45, 80.00, TRUE),
('Botox Capilar', 'Reposição de nutrientes e proteínas para cabelos danificados. Efeito liso e sedoso.', 'Cabelo', 90, 180.00, TRUE),
('Cauterização', 'Reconstrução profunda da fibra capilar. Ideal para cabelos muito danificados.', 'Cabelo', 120, 200.00, TRUE),
('Escova Progressiva', 'Alisamento duradouro com fórmula sem formol. Fios lisos e brilhantes.', 'Cabelo', 180, 350.00, TRUE),
-- Pacotes Especiais
('Day Spa Completo', 'Pacote completo: limpeza facial + massagem relaxante + manicure. 4 horas de puro relaxamento.', 'Pacotes', 240, 450.00, TRUE),
('Ritual da Noiva', 'Preparação completa para o grande dia: facial, corporal, cabelo, mãos e pés.', 'Pacotes', 360, 800.00, TRUE),
('Spa das Amigas', 'Pacote para 4 pessoas: massagem + facial + manicure. Perfeito para celebrar juntas!', 'Pacotes', 180, 1200.00, TRUE);

-- Inserir clientes de exemplo
INSERT INTO clients (name, phone, email, notes, total_visits, last_visit) VALUES
('Maria Silva', '(11) 99999-1111', 'maria@email.com', 'Cliente VIP, prefere horários pela manhã', 12, NOW() - INTERVAL 2 DAY),
('Ana Costa', '(11) 99999-2222', 'ana@email.com', '', 8, NOW() - INTERVAL 4 DAY),
('Juliana Santos', '(11) 99999-3333', 'juliana@email.com', 'Alergia a produtos com parabenos', 5, NOW() - INTERVAL 7 DAY),
('Carla Oliveira', '(11) 99999-4444', 'carla@email.com', '', 15, NOW() - INTERVAL 12 DAY),
('Patricia Lima', '(11) 99999-5555', 'patricia@email.com', 'Nova cliente, indicação da Maria Silva', 3, NOW() - INTERVAL 17 DAY);

-- Inserir agendamentos de exemplo (para hoje)
INSERT INTO appointments (client_id, service_id, appointment_date, appointment_time, room, status) VALUES
(1, 1, CURDATE(), '09:00:00', 'Sala 1', 'CONFIRMADO'),
(2, 2, CURDATE(), '10:30:00', 'Sala 2', 'PENDENTE'),
(3, 3, CURDATE(), '11:00:00', 'Sala 1', 'CONFIRMADO'),
(4, 1, CURDATE(), '14:00:00', 'Sala 2', 'CONFIRMADO'),
(5, 4, CURDATE(), '15:30:00', 'Sala 1', 'PENDENTE'),
(1, 3, CURDATE() + INTERVAL 1 DAY, '10:00:00', 'Sala 1', 'CONFIRMADO'),
(2, 5, CURDATE() + INTERVAL 2 DAY, '14:30:00', 'Sala 2', 'PENDENTE');

-- Inserir campanhas de exemplo
INSERT INTO campaigns (title, message, status, target_audience, sent_date, recipients, opens) VALUES
('Promoção de Verão', 'Aproveite 20% de desconto em todos os tratamentos faciais!', 'ENVIADA', 'all', NOW() - INTERVAL 2 DAY, 45, 38),
('Lançamento Detox', 'Conheça nosso novo tratamento de Detox Facial Premium!', 'AGENDADA', 'active', NULL, 120, 0),
('Aniversário Musa', 'Comemore conosco! Condições especiais para você.', 'RASCUNHO', 'vip', NULL, 0, 0);

-- Inserir configurações padrão
INSERT INTO configurations (config_key, config_value, config_group, description) VALUES
('clinicName', 'Musa SPA', 'general', 'Nome da clínica'),
('phone', '(11) 94979-1718', 'general', 'Telefone principal'),
('email', 'contato@musasp.com.br', 'general', 'E-mail de contato'),
('address', 'Alameda Rio Negro, 1000 - Alphaville', 'general', 'Endereço'),
('newAppointment', 'true', 'notifications', 'Notificar novo agendamento'),
('clientConfirmation', 'true', 'notifications', 'Notificar confirmação de cliente'),
('humanAttendance', 'true', 'notifications', 'Notificar atendimento humano'),
('appointmentReminder', 'true', 'automation', 'Lembrete de agendamento'),
('autoConfirmation', 'true', 'automation', 'Confirmação automática'),
('postServiceMessage', 'false', 'automation', 'Mensagem pós-atendimento');

-- Inserir contatos WhatsApp de exemplo
INSERT INTO whatsapp_contacts (name, phone, status, stage, last_message, unread) VALUES
('Maria Silva', '(11) 99999-1111', 'ATIVO', 'Agendamento', NOW() - INTERVAL 2 MINUTE, 2),
('Ana Costa', '(11) 99999-2222', 'HUMANO', 'Atendimento Humano', NOW() - INTERVAL 5 MINUTE, 1),
('Juliana Santos', '(11) 99999-3333', 'AGUARDANDO', 'Confirmação', NOW() - INTERVAL 15 MINUTE, 0),
('Carla Oliveira', '(11) 99999-4444', 'ATIVO', 'Boas-vindas', NOW() - INTERVAL 30 MINUTE, 0),
('Patricia Lima', '(11) 99999-5555', 'FINALIZADO', 'Agendamento Confirmado', NOW() - INTERVAL 1 HOUR, 0);

-- ============================================
-- FIM DO SCRIPT
-- ============================================
