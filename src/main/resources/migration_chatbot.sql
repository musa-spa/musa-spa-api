-- ============================================
-- Script de Migração - Adiciona suporte ao Chatbot
-- Execute este script se já possui o banco criado
-- ============================================

USE musa;

-- Adiciona CPF na tabela clients (se não existir)
SET @dbname = DATABASE();
SET @tablename = "clients";
SET @columnname = "cpf";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @dbname
    AND TABLE_NAME = @tablename
    AND COLUMN_NAME = @columnname
  ) > 0,
  "SELECT 1",
  CONCAT("ALTER TABLE ", @tablename, " ADD ", @columnname, " VARCHAR(11) UNIQUE AFTER name")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Cria tabela chat_sessions (se não existir)
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

-- Limpa serviços antigos e insere novos (CUIDADO: isso apaga agendamentos existentes!)
-- Descomente as linhas abaixo se quiser atualizar os serviços

-- DELETE FROM appointments;
-- DELETE FROM services;

-- Insere novos serviços se a tabela estiver vazia
INSERT INTO services (name, description, category, duration, price, active)
SELECT * FROM (
    SELECT 'Limpeza de Pele Profunda' as name, 'Remoção de impurezas, extração de cravos, esfoliação e hidratação profunda. Indicado para todos os tipos de pele.' as description, 'Facial' as category, 60 as duration, 180.00 as price, TRUE as active
    UNION ALL SELECT 'Peeling Químico', 'Renovação celular com ácidos específicos. Trata manchas, linhas finas e textura irregular. Requer avaliação prévia.', 'Facial', 45, 250.00, TRUE
    UNION ALL SELECT 'Hydrafacial', 'Tecnologia 3 em 1: limpeza, esfoliação e hidratação com infusão de séruns. Resultado imediato e sem downtime.', 'Facial', 90, 380.00, TRUE
    UNION ALL SELECT 'Radiofrequência Facial', 'Estímulo de colágeno para firmeza e redução de flacidez. Efeito lifting natural.', 'Facial', 50, 220.00, TRUE
    UNION ALL SELECT 'Manicure Tradicional', 'Cuticulagem, lixamento e esmaltação profissional. Unhas perfeitas e bem cuidadas.', 'Manicure', 40, 45.00, TRUE
    UNION ALL SELECT 'Pedicure Completa', 'Tratamento completo dos pés com esfoliação, hidratação e esmaltação.', 'Manicure', 50, 55.00, TRUE
    UNION ALL SELECT 'Spa dos Pés', 'Tratamento premium com escalda-pés, esfoliação, massagem relaxante e esmaltação.', 'Manicure', 90, 120.00, TRUE
    UNION ALL SELECT 'Alongamento em Gel', 'Unhas alongadas com gel de alta durabilidade. Incluí manutenção após 21 dias.', 'Manicure', 120, 180.00, TRUE
    UNION ALL SELECT 'Massagem Relaxante', 'Massagem corporal com óleos essenciais para relaxamento total e alívio do estresse.', 'Corporal', 60, 200.00, TRUE
    UNION ALL SELECT 'Massagem Modeladora', 'Massagem intensiva para redução de medidas, combate à celulite e definição corporal.', 'Corporal', 50, 180.00, TRUE
    UNION ALL SELECT 'Drenagem Linfática', 'Técnica manual para eliminar toxinas, reduzir inchaço e retenção de líquidos.', 'Corporal', 60, 170.00, TRUE
    UNION ALL SELECT 'Esfoliação Corporal', 'Renovação da pele com esfoliantes naturais seguida de hidratação profunda.', 'Corporal', 45, 150.00, TRUE
    UNION ALL SELECT 'Hidratação Capilar', 'Tratamento intensivo para recuperar brilho, maciez e vitalidade dos fios.', 'Cabelo', 45, 80.00, TRUE
    UNION ALL SELECT 'Botox Capilar', 'Reposição de nutrientes e proteínas para cabelos danificados. Efeito liso e sedoso.', 'Cabelo', 90, 180.00, TRUE
    UNION ALL SELECT 'Cauterização', 'Reconstrução profunda da fibra capilar. Ideal para cabelos muito danificados.', 'Cabelo', 120, 200.00, TRUE
    UNION ALL SELECT 'Escova Progressiva', 'Alisamento duradouro com fórmula sem formol. Fios lisos e brilhantes.', 'Cabelo', 180, 350.00, TRUE
    UNION ALL SELECT 'Day Spa Completo', 'Pacote completo: limpeza facial + massagem relaxante + manicure. 4 horas de puro relaxamento.', 'Pacotes', 240, 450.00, TRUE
    UNION ALL SELECT 'Ritual da Noiva', 'Preparação completa para o grande dia: facial, corporal, cabelo, mãos e pés.', 'Pacotes', 360, 800.00, TRUE
    UNION ALL SELECT 'Spa das Amigas', 'Pacote para 4 pessoas: massagem + facial + manicure. Perfeito para celebrar juntas!', 'Pacotes', 180, 1200.00, TRUE
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name = tmp.name);

SELECT 'Migração concluída com sucesso!' as status;
